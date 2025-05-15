package com.main.controller;

import com.main.component.RedisComponet;
import com.main.entity.po.Douban;
import com.main.entity.po.Juejin;
import com.main.entity.po.ThePaper;
import com.main.entity.po.Tskr;
import com.main.entity.vo.ResponseVO;
import com.main.service.impl.DoubanService;
import com.main.service.impl.JuejinService;
import com.main.service.impl.ThePaperService;
import com.main.service.impl.TskrService;
import com.main.task.MyScheduledTask;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hotlist")
public class hotListController extends ABaseController{
    @Resource
    private ThePaperService thePaperService;
    @Resource
    private RedisComponet redisComponet;
    @Resource
    private JuejinService juejinService;
    @Resource
    private DoubanService doubanService;
    @Resource
    private TskrService tskrService;

    private static final Logger logger = LoggerFactory.getLogger(hotListController.class);


    @RequestMapping("/paper")
    public ResponseVO Paperhot() {
        List<ThePaper> thePaperList;
        thePaperList = redisComponet.getPaperList();
        if(thePaperList != null && !thePaperList.isEmpty()){
            logger.info("从Redis获取澎湃热点数据成功!");
        }else {
            thePaperList = thePaperService.fetchHotNews();
            redisComponet.setPaperList(thePaperList);
            logger.info("保存澎湃热点数据到Redis!");
        }
        return getSuccessResponseVO(thePaperList);
    }

    @RequestMapping("/juejin")
    public ResponseVO Juejinhot(){
        List<Juejin> juejinList;
        juejinList = redisComponet.getJuejinList();
        if(juejinList != null && !juejinList.isEmpty()){
            logger.info("从Redis获取掘金数据成功!");
        }else {
            juejinList = juejinService.getJuejinData();
            redisComponet.setJuejinList(juejinList);
            logger.info("保存掘金数据到Redis!");
        }
        return getSuccessResponseVO(juejinList);
    }

    @RequestMapping("/doubanea")
    public ResponseVO DoubanEA(){
        List<Douban> doubanList;
        doubanList = redisComponet.getDoubanList();
        if(doubanList != null && !doubanList.isEmpty()){
            logger.info("从Redis获取豆瓣数据成功!");

        }else{
            doubanList = doubanService.fecthDoubanData();
            redisComponet.setDoubanList(doubanList);
        }
        return getSuccessResponseVO(doubanList);
    }

    @RequestMapping("/36kr")
    public ResponseVO Tskr(){
        List<Tskr> tskrList;
        tskrList = redisComponet.getTskrList();
        if(tskrList != null && !tskrList.isEmpty()){
            logger.info("从Redis获取36kr数据成功!");
        }else {
            tskrList = tskrService.fetchTskrData();
            redisComponet.setTskrList(tskrList);
        }
        return getSuccessResponseVO(tskrList);
    }


}
