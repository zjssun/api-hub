package com.main.service;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.main.entity.po.User;
import com.main.exception.BusinessException;
import com.main.mappers.UserMapper;
import com.main.redis.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    /** 中国大陆手机号 */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    /** 注意：RedisUtils.setex/expire 的 time 单位是毫秒（项目既有约定） */
    private static final long CODE_RESEND_MS = 60_000;
    private static final long TOKEN_TTL_MS = 7L * 24 * 3600 * 1000;

    /** 重发限频（验证码本体由 PNS 生成并代管，服务端不存码） */
    private static final String KEY_RATE = "vc:rate:";
    private static final String KEY_TOKEN = "auth:token:";

    private final UserMapper userMapper;
    private final RedisUtils<Object> redisUtils;
    private final SmsService smsService;
    private final SensitiveWordBs sensitiveWordBs;

    public AuthService(UserMapper userMapper,
                       RedisUtils<Object> redisUtils,
                       SmsService smsService,
                       SensitiveWordBs sensitiveWordBs) {
        this.userMapper = userMapper;
        this.redisUtils = redisUtils;
        this.smsService = smsService;
        this.sensitiveWordBs = sensitiveWordBs;
    }

    /* ---------- 验证码 ---------- */

    /** 发送注册验证码：手机号格式 + 未注册 + 昵称敏感词 + 60s 限频 */
    public void sendRegisterCode(String phone, String nickname) {
        checkPhone(phone);
        checkNickname(nickname);
        if (userMapper.findByPhone(phone) != null) {
            throw new BusinessException("该手机号已注册，请直接登录");
        }
        checkRateLimit(phone);
        smsService.sendVerifyCode(phone);
        redisUtils.setex(KEY_RATE + phone, "1", CODE_RESEND_MS);
    }

    /** 发送重置密码验证码：手机号须已注册 */
    public void sendResetCode(String phone) {
        checkPhone(phone);
        if (userMapper.findByPhone(phone) == null) {
            throw new BusinessException("该手机号未注册");
        }
        checkRateLimit(phone);
        smsService.sendVerifyCode(phone);
        redisUtils.setex(KEY_RATE + phone, "1", CODE_RESEND_MS);
    }

    /* ---------- 注册 / 登录 / 重置 ---------- */

    public Map<String, Object> register(String phone, String nickname, String password, String code) {
        checkPhone(phone);
        checkNickname(nickname);
        checkPassword(password);
        verifyCode(phone, code);
        if (userMapper.findByPhone(phone) != null) {
            throw new BusinessException("该手机号已注册，请直接登录");
        }
        User user = new User();
        user.setPhone(phone);
        user.setNickname(nickname.trim());
        user.setPasswordHash(ENCODER.encode(password));
        user.setPlan("free");
        user.setCreatedAt(LocalDateTime.now());
        userMapper.insert(user);

        logger.info("新用户注册 phone={}", phone);
        return loginResult(user, issueToken(user.getId()));
    }

    public Map<String, Object> login(String phone, String password) {
        checkPhone(phone);
        User user = userMapper.findByPhone(phone);
        if (user == null || !StringUtils.hasText(password) || !ENCODER.matches(password, user.getPasswordHash())) {
            throw new BusinessException("手机号或密码错误");
        }
        return loginResult(user, issueToken(user.getId()));
    }

    public void resetPassword(String phone, String code, String newPassword) {
        checkPhone(phone);
        checkPassword(newPassword);
        verifyCode(phone, code);
        User user = userMapper.findByPhone(phone);
        if (user == null) {
            throw new BusinessException("该手机号未注册");
        }
        userMapper.updatePassword(user.getId(), ENCODER.encode(newPassword));
    }

    /* ---------- token ---------- */

    public String issueToken(long userId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        redisUtils.setex(KEY_TOKEN + token, userId, TOKEN_TTL_MS);
        return token;
    }

    /** token → userId，无效返回 null */
    public Long resolveToken(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        Object v = redisUtils.get(KEY_TOKEN + token.trim());
        if (v instanceof Number n) {
            return n.longValue();
        }
        if (v != null) {
            try {
                return Long.parseLong(v.toString());
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    public void logout(String token) {
        if (StringUtils.hasText(token)) {
            redisUtils.delete(KEY_TOKEN + token.trim());
        }
    }

    public User findUser(long userId) {
        return userMapper.findById(userId);
    }

    /** 是否管理员（is_admin=1）：豁免 service.closed 总开关等场景使用 */
    public boolean isAdmin(Long userId) {
        if (userId == null) {
            return false;
        }
        User user = findUser(userId);
        return user != null && user.isAdmin();
    }

    public Map<String, Object> updateNickname(String token, String nickname) {
        Long userId = resolveToken(token);
        if (userId == null) {
            throw new BusinessException("登录已失效，请重新登录");
        }
        checkNickname(nickname);
        userMapper.updateNickname(userId, nickname.trim());
        User user = findUser(userId);
        if (user == null) {
            throw new BusinessException("账号不存在");
        }
        return publicUser(user);
    }

    /* ---------- 校验 ---------- */

    private void checkPhone(String phone) {
        if (!StringUtils.hasText(phone) || !PHONE_PATTERN.matcher(phone.trim()).matches()) {
            throw new BusinessException("手机号格式不正确");
        }
    }

    private void checkNickname(String nickname) {
        if (!StringUtils.hasText(nickname) || nickname.trim().length() > 32) {
            throw new BusinessException("昵称不能为空且不超过 32 字");
        }
        if (sensitiveWordBs.contains(nickname.trim())) {
            throw new BusinessException("昵称不宜使用，请换一个");
        }
    }

    private void checkPassword(String password) {
        if (!StringUtils.hasText(password) || password.length() < 6 || password.length() > 64) {
            throw new BusinessException("密码长度需 6~64 位");
        }
    }

    private void checkRateLimit(String phone) {
        if (redisUtils.keyExists(KEY_RATE + phone)) {
            throw new BusinessException("验证码发送太频繁，请 60 秒后再试");
        }
    }

    /** 验证码核验走 PNS CheckSmsVerifyCode（PNS 代管有效期） */
    private void verifyCode(String phone, String code) {
        if (!smsService.checkVerifyCode(phone, code)) {
            throw new BusinessException("验证码错误或已过期");
        }
    }

    private Map<String, Object> loginResult(User user, String token) {
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", publicUser(user));
        return result;
    }

    private Map<String, Object> publicUser(User user) {
        Map<String, Object> u = new HashMap<>();
        u.put("id", user.getId());
        u.put("phone", user.getPhone());
        u.put("nickname", user.getNickname());
        u.put("avatar", user.getAvatar());
        u.put("plan", StringUtils.hasText(user.getPlan()) ? user.getPlan() : "free");
        return u;
    }
}
