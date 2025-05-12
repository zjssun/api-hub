package com.main.controller;

import com.main.component.RedisComponet;
import com.main.entity.po.ThePaper;
import com.main.entity.vo.ResponseVO;
import com.main.service.impl.ThePaperService;
import com.main.task.MyScheduledTask;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hotList")
public class hotListController extends ABaseController{
    @Resource
    private ThePaperService thePaperService;
    @Resource
    private RedisComponet redisComponet;

    private static final Logger logger = LoggerFactory.getLogger(hotListController.class);


    @RequestMapping("/Paperhot")
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


}
