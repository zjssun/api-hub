package com.main.component;

import com.main.constant.Constants;
import com.main.entity.po.ThePaper;
import com.main.redis.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisComponet {
    @Resource
    private RedisUtils redisUtils;

    public List<ThePaper> getPaperList(){
       return (List<ThePaper>) redisUtils.get(Constants.THE_PAPER_REDIS_KEY);
    }

    public void setPaperList(List<ThePaper> paperList){
        redisUtils.setex(Constants.THE_PAPER_REDIS_KEY, paperList, Constants.REDIS_TIME_THRITY_MIN);
    }
}
