package com.main.service;

import com.main.entity.enums.PlayerEnum;
import com.main.entity.po.PlayerMatch;
import com.main.mappers.PlayerMatchMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class PlayerMatchService {
    private static final Logger logger = LoggerFactory.getLogger(PlayerMatchService.class);
    private static final Pattern SAFE_TABLE_NAME = Pattern.compile("[a-z0-9_]+");

    private final PlayerMatchMapper playerMatchMapper;

    public PlayerMatchService(PlayerMatchMapper playerMatchMapper) {
        this.playerMatchMapper = playerMatchMapper;
    }

    public boolean save(PlayerEnum playerEnum, PlayerMatch data) {
        try {
            return playerMatchMapper.insert(resolveTableName(playerEnum), data) > 0;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }

    public int deleteOutdatedData(PlayerEnum playerEnum, long retentionPeriodMillis) {
        long cutoffTimestamp = System.currentTimeMillis() - retentionPeriodMillis;
        return playerMatchMapper.deleteOlderThan(resolveTableName(playerEnum), cutoffTimestamp);
    }

    public List<PlayerMatch> findAllMatchesByPlayerName(String playerName) {
        Optional<PlayerEnum> playerEnumOptional = PlayerEnum.fromName(playerName);
        logger.info("获取玩家{}的数据", playerName);
        if (playerEnumOptional.isEmpty()) {
            logger.info("未找到玩家{}的数据", playerName);
            return Collections.emptyList();
        }
        return playerMatchMapper.findAllOrderByTimestampDesc(resolveTableName(playerEnumOptional.get()));
    }

    private String resolveTableName(PlayerEnum playerEnum) {
        String tableName = playerEnum.getName();
        if (!SAFE_TABLE_NAME.matcher(tableName).matches()) {
            throw new IllegalStateException("非法表名: " + tableName);
        }
        return tableName;
    }
}
