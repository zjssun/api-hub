package com.main.service;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.main.entity.po.User;
import com.main.exception.BusinessException;
import com.main.mappers.UserMapper;
import com.main.redis.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();
    private static final SecureRandom RANDOM = new SecureRandom();

    /** 注意：RedisUtils.setex/expire 的 time 单位是毫秒（项目既有约定） */
    private static final long CODE_TTL_MS = 300_000;
    private static final long CODE_RESEND_MS = 60_000;
    private static final long TOKEN_TTL_MS = 7L * 24 * 3600 * 1000;

    private static final String KEY_REG_CODE = "vc:reg:";
    private static final String KEY_RESET_CODE = "vc:reset:";
    private static final String KEY_RATE = "vc:rate:";
    private static final String KEY_TOKEN = "auth:token:";

    private final UserMapper userMapper;
    private final RedisUtils<Object> redisUtils;
    private final JavaMailSender mailSender;
    private final SensitiveWordBs sensitiveWordBs;
    private final String mailFrom;

    public AuthService(UserMapper userMapper,
                       RedisUtils<Object> redisUtils,
                       JavaMailSender mailSender,
                       SensitiveWordBs sensitiveWordBs,
                       @Value("${spring.mail.username:}") String mailFrom) {
        this.userMapper = userMapper;
        this.redisUtils = redisUtils;
        this.mailSender = mailSender;
        this.sensitiveWordBs = sensitiveWordBs;
        this.mailFrom = mailFrom;
    }

    /* ---------- 验证码 ---------- */

    /** 发送注册验证码：邮箱格式 + 未注册 + 昵称敏感词 + 60s 限频 */
    public void sendRegisterCode(String email, String nickname) {
        checkEmail(email);
        checkNickname(nickname);
        if (userMapper.findByEmail(email) != null) {
            throw new BusinessException("该邮箱已注册，请直接登录");
        }
        checkRateLimit(email);
        sendCode(email, KEY_REG_CODE, "注册验证码");
    }

    /** 发送重置密码验证码：邮箱须已注册 */
    public void sendResetCode(String email) {
        checkEmail(email);
        if (userMapper.findByEmail(email) == null) {
            throw new BusinessException("该邮箱未注册");
        }
        checkRateLimit(email);
        sendCode(email, KEY_RESET_CODE, "重置密码验证码");
    }

    private void sendCode(String email, String keyPrefix, String purpose) {
        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        try {
            var mime = mailSender.createMimeMessage();
            var helper = new org.springframework.mail.javamail.MimeMessageHelper(mime, false, "UTF-8");
            helper.setFrom(mailFrom, "管油阿特");
            helper.setTo(email);
            helper.setSubject("【管油阿特】" + purpose);
            helper.setText("您的" + purpose + "是：" + code + "，5 分钟内有效。若非本人操作请忽略本邮件。");
            mailSender.send(mime);
        } catch (Exception e) {
            logger.warn("验证码邮件发送失败 {}: {}", email, e.getMessage());
            throw new BusinessException("验证码发送失败，请稍后重试");
        }
        redisUtils.setex(keyPrefix + email, code, CODE_TTL_MS);
        redisUtils.setex(KEY_RATE + email, "1", CODE_RESEND_MS);
    }

    /* ---------- 注册 / 登录 / 重置 ---------- */

    public Map<String, Object> register(String email, String nickname, String password, String code) {
        checkEmail(email);
        checkNickname(nickname);
        checkPassword(password);
        verifyCode(code, KEY_REG_CODE + email);
        if (userMapper.findByEmail(email) != null) {
            throw new BusinessException("该邮箱已注册，请直接登录");
        }
        User user = new User();
        user.setEmail(email);
        user.setNickname(nickname.trim());
        user.setPasswordHash(ENCODER.encode(password));
        user.setPlan("free");
        user.setCreatedAt(LocalDateTime.now());
        userMapper.insert(user);

        logger.info("新用户注册 email={}", email);
        return loginResult(user, issueToken(user.getId()));
    }

    public Map<String, Object> login(String email, String password) {
        checkEmail(email);
        User user = userMapper.findByEmail(email);
        if (user == null || !StringUtils.hasText(password) || !ENCODER.matches(password, user.getPasswordHash())) {
            throw new BusinessException("邮箱或密码错误");
        }
        return loginResult(user, issueToken(user.getId()));
    }

    public void resetPassword(String email, String code, String newPassword) {
        checkEmail(email);
        checkPassword(newPassword);
        verifyCode(code, KEY_RESET_CODE + email);
        User user = userMapper.findByEmail(email);
        if (user == null) {
            throw new BusinessException("该邮箱未注册");
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

    private void checkEmail(String email) {
        if (!StringUtils.hasText(email) || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new BusinessException("邮箱格式不正确");
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

    private void checkRateLimit(String email) {
        if (redisUtils.keyExists(KEY_RATE + email)) {
            throw new BusinessException("验证码发送太频繁，请 60 秒后再试");
        }
    }

    private void verifyCode(String code, String key) {
        Object saved = redisUtils.get(key);
        if (!StringUtils.hasText(code) || saved == null || !code.trim().equals(saved.toString())) {
            throw new BusinessException("验证码错误或已过期");
        }
        redisUtils.delete(key);
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
        u.put("email", user.getEmail());
        u.put("nickname", user.getNickname());
        u.put("avatar", user.getAvatar());
        u.put("plan", StringUtils.hasText(user.getPlan()) ? user.getPlan() : "free");
        return u;
    }
}
