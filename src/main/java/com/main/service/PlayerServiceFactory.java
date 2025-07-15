package com.main.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.main.entity.enums.PlayerEnum;
import com.main.entity.po.PlayerMatchData;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlayerServiceFactory {
    private static final Logger logger = LoggerFactory.getLogger(PlayerServiceFactory.class);
    @Autowired
    private List<BaseMapper<? extends PlayerMatchData>> mappers;

    private final Map<Class<? extends PlayerMatchData>, BaseMapper<? extends PlayerMatchData>> mapperMap = new HashMap<>();

    @PostConstruct
    public void init(){
        for(BaseMapper<? extends PlayerMatchData> mapper : mappers){
            //获取BaseMappe<? extends PlayerMatchData>的类型信息
            ResolvableType resolvableType = ResolvableType.forClass(mapper.getClass()).as(BaseMapper.class);
            // 获取玩家类的类型信息
            Class<?> entityType = resolvableType.getGeneric(0).resolve();

            if(entityType != null && PlayerMatchData.class.isAssignableFrom(entityType)){
                //保存为[Donk:DonkMapper]
                mapperMap.put((Class<? extends PlayerMatchData>) entityType,mapper);
            }
        }
    }

    public Optional<BaseMapper<? extends PlayerMatchData>> getMapper(PlayerEnum playerEnum){
        Class<? extends PlayerMatchData> entityClass = playerEnum.getDataClass();
        return Optional.ofNullable(mapperMap.get(entityClass));
    }

    //保存数据
    public <T extends PlayerMatchData> boolean save(PlayerEnum playerEnum,T data){
        return getMapper(playerEnum).map(mapper -> {
            try {
                @SuppressWarnings("unchecked")
                BaseMapper<T> specificMapper = (BaseMapper<T>) mapper;
                specificMapper.insert(data);
                return true;
            }catch (DataIntegrityViolationException e){
                return false;
            }
        }).orElse(false);
    }

    //删除过期数据
    private <T extends PlayerMatchData> int performTyepedDelete(BaseMapper<T>mapper,long cutoffTimestamp){
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.lt("timestamp",cutoffTimestamp);
        return mapper.delete(queryWrapper);
    }
    public int deleteOutdatedData (PlayerEnum playerEnum, long retentionPeriodMillis){
        long cutoffTimestamp = System.currentTimeMillis() - retentionPeriodMillis;
        return getMapper(playerEnum)
                .map(mapper -> performTyepedDelete(mapper, cutoffTimestamp))
                .orElse(0);
    }

    //获取某玩家的所有数据
    public List<? extends PlayerMatchData> findAllMatchesByPlayerName(String playerName){
        Optional<PlayerEnum> playerEnumOptional = PlayerEnum.fromName(playerName);
        logger.info("获取玩家{}的数据",playerName);
        if(!playerEnumOptional.isPresent()) {
            logger.info("未找到玩家{}的数据",playerName);
            return Collections.emptyList();
        }
        return getMapper(playerEnumOptional.get())
                .map(this::performFindAllAndSort)
                .orElse(Collections.emptyList());
    }

    private <T extends PlayerMatchData> List<T> performFindAllAndSort(BaseMapper<T> mapper) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("timestamp");
        return mapper.selectList(queryWrapper);
    }
}
