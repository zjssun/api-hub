package com.main.service;

import com.main.entity.enums.PlayerEnum;
import com.main.entity.po.PlayerMatch;
import com.main.mappers.PlayerMatchMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlayerMatchServiceTest {
    @Mock
    private PlayerMatchMapper playerMatchMapper;

    @InjectMocks
    private PlayerMatchService playerMatchService;

    @Test
    void saveReturnsTrueWhenInsertSucceeds() {
        PlayerMatch playerMatch = new PlayerMatch();
        playerMatch.setMatchId("1-match-a");
        when(playerMatchMapper.insertIfAbsent(eq("donk"), eq(playerMatch))).thenReturn(1);

        boolean saved = playerMatchService.save(PlayerEnum.DONK, playerMatch);

        assertTrue(saved);
    }

    @Test
    void saveReturnsFalseWhenMatchAlreadyExists() {
        PlayerMatch playerMatch = new PlayerMatch();
        playerMatch.setMatchId("1-match-a");
        when(playerMatchMapper.insertIfAbsent(eq("donk"), eq(playerMatch))).thenReturn(0);

        boolean saved = playerMatchService.save(PlayerEnum.DONK, playerMatch);

        assertFalse(saved);
    }

    @Test
    void saveReturnsFalseWhenMatchIdIsMissing() {
        PlayerMatch playerMatch = new PlayerMatch();

        boolean saved = playerMatchService.save(PlayerEnum.DONK, playerMatch);

        assertFalse(saved);
        verify(playerMatchMapper, never()).insertIfAbsent(any(), any());
    }

    @Test
    void saveReturnsFalseWhenInsertHitsConstraint() {
        PlayerMatch playerMatch = new PlayerMatch();
        playerMatch.setMatchId("1-match-a");
        when(playerMatchMapper.insertIfAbsent(eq("donk"), eq(playerMatch)))
                .thenThrow(new DataIntegrityViolationException("duplicate"));

        boolean saved = playerMatchService.save(PlayerEnum.DONK, playerMatch);

        assertFalse(saved);
    }
}
