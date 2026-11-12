package com.main.service;

import com.main.entity.po.User;
import com.main.mappers.UserMapper;
import com.main.redis.RedisUtils;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UsageServiceTest {
    @Test
    void adminQuotaAppliesToBothUsageAndDownloadsEvenWithACustomQuota() {
        RedisUtils<Long> redis = mock();
        UserMapper users = mock();
        UsageService service = new UsageService(redis, users, 100, 3072, 30720);
        long mb = 1024L * 1024;
        User account = new User();
        when(users.findById(7L)).thenReturn(account);
        when(redis.get(anyString())).thenReturn(2 * mb);
        assertEquals(100 * mb, service.resolveDailyQuota(null));
        assertEquals(3072 * mb, service.resolveDailyQuota(7L));
        account.setCustomQuotaMb(200L);
        assertEquals(200 * mb, service.resolveDailyQuota(7L));
        account.setAdmin(true);
        long adminQuota = 30 * 1024 * mb;
        var card = service.usageInfo(7L, null);
        assertEquals(adminQuota, card.get("quotaBytes"));
        assertEquals(adminQuota - 2 * mb, card.get("remainingBytes"));
        assertEquals(adminQuota - 2 * mb, service.remainingBytes(7L, null));
    }

    @Test
    void usageAndCountdownAdvanceTogetherAtServerMidnight() {
        RedisUtils<Long> redis = mock();
        UsageService service = new UsageService(redis, mock(UserMapper.class), 100, 3072, 30720);
        ZoneId zone = ZoneId.of("Asia/Shanghai");
        var before = ZonedDateTime.of(2026, 11, 11, 23, 59, 59, 0, zone);
        var midnight = before.plusSeconds(1);
        var nextMidnight = midnight.plusDays(1);
        when(redis.get("quota:20261111:v:visitor")).thenReturn(123L);
        when(redis.get("quota:20261112:v:visitor")).thenReturn(null);

        try (var clock = mockStatic(ZonedDateTime.class, CALLS_REAL_METHODS)) {
            clock.when(ZonedDateTime::now).thenReturn(before, midnight);
            var previous = service.usageInfo(null, "visitor");
            assertEquals(123L, previous.get("usedBytes"));
            assertEquals(before.toInstant().toEpochMilli(), previous.get("serverTimeMs"));
            assertEquals(midnight.toInstant().toEpochMilli(), previous.get("resetAtMs"));
            var current = service.usageInfo(null, "visitor");
            assertEquals(0L, current.get("usedBytes"));
            assertEquals(nextMidnight.toInstant().toEpochMilli(), current.get("resetAtMs"));
            verify(redis).get("quota:20261112:v:visitor");
        }
    }
}
