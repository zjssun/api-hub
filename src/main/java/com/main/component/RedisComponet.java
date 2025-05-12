package com.main.component;

import com.main.constant.Constants;
import com.main.entity.po.Douban;
import com.main.entity.po.Juejin;
import com.main.entity.po.ThePaper;
import com.main.redis.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisComponet {
    @Resource
    private RedisUtils redisUtils;

    //获取缓存
    public List<ThePaper> getPaperList(){
       return (List<ThePaper>) redisUtils.get(Constants.THE_PAPER_REDIS_KEY);
    }
    public List<Juejin> getJuejinList(){
        return (List<Juejin>) redisUtils.get(Constants.JUEJIN_REDIS_KEY);
    }
    public List<Douban> getDoubanList(){
        return (List<Douban>) redisUtils.get(Constants.DOUBAN_REDIS_KEY);
    }

    //设置缓存
    public void setPaperList(List<ThePaper> paperList){
        redisUtils.setex(Constants.THE_PAPER_REDIS_KEY, paperList, Constants.REDIS_TIME_THRITY_MIN);
    }
    public void setJuejinList(List<Juejin> juejinList){
        redisUtils.setex(Constants.JUEJIN_REDIS_KEY, juejinList, Constants.REDIS_TIME_THRITY_MIN);
    }
    public void setDoubanList(List<Douban> doubanList){
        redisUtils.setex(Constants.DOUBAN_REDIS_KEY, doubanList, Constants.REDIS_TIME_THRITY_MIN);
    }

}
