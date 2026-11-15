package com.main.service;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.DefaultCredentialProvider;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import com.main.exception.BusinessException;
import com.main.redis.RedisUtils;
import darabonba.core.client.ClientOverrideConfiguration;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.util.Map;

/**
 * 阿里云短信服务（Dysmsapi）验证码。
 * 验证码由本服务生成（6 位数字），Redis 存 5 分钟，核验后即焚。
 */
@Service
public class SmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    /** 注意：RedisUtils.setex 的 time 单位是毫秒（项目既有约定） */
    private static final long CODE_TTL_MS = 300_000;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String KEY_SMS_CODE = "vc:sms:";

    /** 发送失败的常见错误码 → 用户可读提示 */
    private static final Map<String, String> SEND_ERROR_HINTS = Map.of(
            "isv.BUSINESS_LIMIT_CONTROL", "短信发送太频繁，请稍后再试",
            "isv.MOBILE_NUMBER_ILLEGAL", "手机号格式不正确",
            "isv.DAY_LIMIT_CONTROL", "今日短信发送次数已达上限",
            "isv.MONTH_LIMIT_CONTROL", "本月短信发送次数已达上限",
            "isv.INVALID_PARAMETERS", "验证码服务配置有误，请联系管理员",
            "isv.SMS_SIGNATURE_ILLEGAL", "验证码服务签名配置有误，请联系管理员",
            "isv.SMS_TEMPLATE_ILLEGAL", "验证码服务模板配置有误，请联系管理员",
            "isv.TEMPLATE_MISSING_PARAMETERS", "验证码服务模板配置有误，请联系管理员"
    );

    private final RedisUtils<String> redisUtils;
    private final String signName;
    private final String templateCode;
    private final String region;
    private final String endpoint;
    private final String accessKeyId;
    private final String accessKeySecret;

    private volatile AsyncClient client;

    public SmsService(RedisUtils<String> redisUtils,
                      @Value("${sms.sign-name:}") String signName,
                      @Value("${sms.template-code:}") String templateCode,
                      @Value("${sms.region:cn-shenzhen}") String region,
                      @Value("${sms.endpoint:dysmsapi.aliyuncs.com}") String endpoint,
                      @Value("${sms.access-key-id:}") String accessKeyId,
                      @Value("${sms.access-key-secret:}") String accessKeySecret) {
        this.redisUtils = redisUtils;
        this.signName = signName != null ? signName.trim() : "";
        this.templateCode = templateCode != null ? templateCode.trim() : "";
        this.region = region;
        this.endpoint = endpoint;
        this.accessKeyId = accessKeyId != null ? accessKeyId.trim() : "";
        this.accessKeySecret = accessKeySecret != null ? accessKeySecret.trim() : "";
    }

    /** 启动时打印生效的短信配置，AK 掩码输出便于确认进程实际拿到的凭据 */
    @PostConstruct
    public void logConfig() {
        logger.info("短信服务配置 endpoint={} region={} signName={} templateCode={} accessKeyId={}(env={})",
                endpoint, region, signName, templateCode,
                mask(accessKeyId), mask(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID")));
    }

    private static String mask(String v) {
        if (!StringUtils.hasText(v)) {
            return "(空)";
        }
        return v.length() <= 10 ? v.charAt(0) + "***" : v.substring(0, 6) + "***" + v.substring(v.length() - 4);
    }

    /** 生成验证码并发送短信，成功后 Redis 存 5 分钟；失败抛 BusinessException */
    public void sendVerifyCode(String phone) {
        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        SendSmsRequest request = SendSmsRequest.builder()
                .phoneNumbers(phone)
                .signName(signName)
                .templateCode(templateCode)
                .templateParam("{\"code\":\"" + code + "\"}")
                .build();
        // 网络类异常（如 DNS 抖动）多为瞬时故障，失败重试一次
        for (int attempt = 1; attempt <= 2; attempt++) {
            try {
                SendSmsResponse resp = client().sendSms(request).get();
                var body = resp.getBody();
                // body.code == "OK" 即发送成功
                if (body != null && "OK".equals(body.getCode())) {
                    redisUtils.setex(KEY_SMS_CODE + phone, code, CODE_TTL_MS);
                    return;
                }
                String resCode = body != null ? body.getCode() : null;
                String message = body != null ? body.getMessage() : null;
                logger.warn("短信验证码发送失败 phone={} code={} message={} ak={} sign={} tpl={}",
                        phone, resCode, message, mask(accessKeyId), signName, templateCode);
                throw new BusinessException(SEND_ERROR_HINTS.getOrDefault(resCode, "验证码发送失败，请稍后重试"));
            } catch (BusinessException e) {
                throw e;
            } catch (Exception e) {
                if (attempt == 1) {
                    logger.warn("短信验证码发送异常，重试一次 phone={}: {}", phone, e.getMessage());
                    continue;
                }
                logger.warn("短信验证码发送异常 phone={} ak={} sign={} tpl={}: {}",
                        phone, mask(accessKeyId), signName, templateCode, e.getMessage());
                throw new BusinessException("请求限流，请多尝试几次");
            }
        }
    }

    /** 核验用户输入的验证码（Redis 比对，成功即焚），true = 通过 */
    public boolean checkVerifyCode(String phone, String code) {
        if (!StringUtils.hasText(code)) {
            return false;
        }
        String key = KEY_SMS_CODE + phone;
        Object saved = redisUtils.get(key);
        if (saved == null || !code.trim().equals(saved.toString())) {
            return false;
        }
        redisUtils.delete(key);
        return true;
    }

    /** 配置 AK/SK 用静态凭据，留空走 DefaultCredentialProvider（env/凭据文件/RAM 链） */
    private AsyncClient client() {
        AsyncClient c = client;
        if (c == null) {
            synchronized (this) {
                if (client == null) {
                    var provider = StringUtils.hasText(accessKeyId) && StringUtils.hasText(accessKeySecret)
                            ? StaticCredentialProvider.create(Credential.builder()
                                    .accessKeyId(accessKeyId)
                                    .accessKeySecret(accessKeySecret)
                                    .build())
                            : DefaultCredentialProvider.builder().build();
                    client = AsyncClient.builder()
                            .region(region)
                            .credentialsProvider(provider)
                            .overrideConfiguration(ClientOverrideConfiguration.create()
                                    .setEndpointOverride(endpoint))
                            .build();
                }
                c = client;
            }
        }
        return c;
    }

    @PreDestroy
    public void shutdown() {
        AsyncClient c = client;
        if (c != null) {
            try {
                c.close();
            } catch (Exception ignored) {
            }
        }
    }
}
