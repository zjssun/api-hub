package com.main.service;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.main.entity.po.User;
import com.main.exception.BusinessException;
import com.main.mappers.UserMapper;
import com.main.redis.RedisUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthNicknameTest {
    private final UserMapper users = mock();
    private final RedisUtils<Object> redis = mock();
    private final SensitiveWordBs words = mock();
    private final AuthService auth = new AuthService(users, redis, null, words, "");

    @Test void updatesOnlyTheAuthenticatedUserAndReturnsTheStoredNickname() {
        when(redis.get("auth:token:session")).thenReturn(19L);
        User user = new User();
        user.setId(19L);
        user.setNickname("新昵称");
        when(users.findById(19L)).thenReturn(user);
        var result = auth.updateNickname("session", " 新昵称 ");
        verify(users).updateNickname(19L, "新昵称");
        assertEquals("新昵称", result.get("nickname"));
        assertFalse(result.containsKey("passwordHash"));
    }

    @Test void rejectsInvalidSessionAndInvalidNicknameBeforeWriting() {
        assertThrows(BusinessException.class, () -> auth.updateNickname("expired", "昵称"));
        when(redis.get("auth:token:session")).thenReturn(19L);
        when(words.contains("禁止")).thenReturn(true);
        for (String name : new String[]{" ", "a".repeat(33), "禁止"}) {
            assertThrows(BusinessException.class, () -> auth.updateNickname("session", name));
        }
        verifyNoInteractions(users);
    }
}
