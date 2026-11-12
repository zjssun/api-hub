package com.main.service;

import com.main.entity.po.User;
import com.main.mappers.UserMapper;
import com.main.redis.RedisUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 每日下载用量配额：Redis 按日 key 计字节，跨日自动归零。
 * 管理员每日固定 30 GB，普通账号优先使用个人额度。
 */
@Service
public class UsageService {

    private static final long MB = 1024L * 1024L;
    /** 注意：RedisUtils.expire 的 time 单位是毫秒（项目既有约定） */
    private static final long QUOTA_KEY_TTL_MS = 48L * 3600 * 1000;

    private final RedisUtils<Long> redisUtils;
    private final UserMapper userMapper;
    private final long anonymousQuotaBytes;
    private final long freeQuotaBytes;
    private final long adminQuotaBytes;

    public UsageService(RedisUtils<Long> redisUtils,
                        UserMapper userMapper,
                        @Value("${quota.anonymous-mb:100}") long anonymousMb,
                        @Value("${quota.free-mb:3072}") long freeMb,
                        @Value("${quota.admin-mb:30720}") long adminMb) {
        this.redisUtils = redisUtils;
        this.userMapper = userMapper;
        this.anonymousQuotaBytes = anonymousMb * MB;
        this.freeQuotaBytes = freeMb * MB;
        this.adminQuotaBytes = adminMb * MB;
    }

    /** 身份主体 key：登录用户 u:{userId}，游客 v:{visitorUid} */
    private String quotaKey(Long userId, String visitorUid, LocalDate day) {
        String subject = userId != null ? "u:" + userId : "v:" + visitorUid;
        return "quota:" + day.format(DateTimeFormatter.BASIC_ISO_DATE) + ":" + subject;
    }

    /** 档位化每日额度解析（付费扩展点） */
    public long resolveDailyQuota(Long userId) {
        return dailyQuota(userId == null ? null : userMapper.findById(userId));
    }

    /** 总额度 = 基础额度（管理员 > 个人覆盖 > 档位）+ 加用量（extra_quota_mb） */
    private long dailyQuota(User user) {
        if (user == null) {
            return anonymousQuotaBytes;
        }
        long base;
        if (user.isAdmin()) {
            base = adminQuotaBytes;
        } else if (user.getCustomQuotaMb() != null && user.getCustomQuotaMb() > 0) {
            base = user.getCustomQuotaMb() * MB;
        } else {
            // 档位映射：后续 pro 等档位在此扩展
            base = freeQuotaBytes;
        }
        Long extra = user.getExtraQuotaMb();
        return base + (extra != null && extra > 0 ? extra * MB : 0);
    }

    public long usedBytes(Long userId, String visitorUid) {
        return usedBytes(userId, visitorUid, LocalDate.now());
    }

    private long usedBytes(Long userId, String visitorUid, LocalDate day) {
        Object used = redisUtils.get(quotaKey(userId, visitorUid, day));
        return used instanceof Number n ? n.longValue() : 0L;
    }

    public long remainingBytes(Long userId, String visitorUid) {
        if (userId == null && !StringUtils.hasText(visitorUid)) {
            return 0;
        }
        return resolveDailyQuota(userId) - usedBytes(userId, visitorUid);
    }

    /** 下载完成后按实际文件大小计费 */
    public void charge(Long userId, String visitorUid, long bytes) {
        if (bytes <= 0 || (userId == null && !StringUtils.hasText(visitorUid))) {
            return;
        }
        String key = quotaKey(userId, visitorUid, LocalDate.now());
        redisUtils.incrementBy(key, bytes);
        redisUtils.expire(key, QUOTA_KEY_TTL_MS);
    }

    /** /api/usage 响应体 */
    public Map<String, Object> usageInfo(Long userId, String visitorUid) {
        User user = userId == null ? null : userMapper.findById(userId);
        long quota = dailyQuota(user);
        ZonedDateTime now = ZonedDateTime.now();
        LocalDate day = now.toLocalDate();
        long used = usedBytes(userId, visitorUid, day);
        Map<String, Object> info = new HashMap<>();
        info.put("serverTimeMs", now.toInstant().toEpochMilli());
        info.put("resetAtMs", day.plusDays(1).atStartOfDay(now.getZone()).toInstant().toEpochMilli());
        info.put("usedBytes", used);
        info.put("quotaBytes", quota);
        info.put("remainingBytes", Math.max(0, quota - used));
        info.put("loggedIn", userId != null);
        if (userId != null) {
            info.put("plan", user != null && StringUtils.hasText(user.getPlan()) ? user.getPlan() : "free");
        } else {
            info.put("plan", "guest");
        }
        return info;
    }
}
