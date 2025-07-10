package com.main.controller;

import com.main.entity.po.PlayerMatchData;
import com.main.entity.vo.ResponseVO;

import com.main.service.PlayerServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/getmatch")
@CrossOrigin(origins = "*")
public class EptController extends ABaseController{
    @Autowired
    private PlayerServiceFactory playerServiceFactory;

    private static final Logger logger = LoggerFactory.getLogger(EptController.class);
    @GetMapping("/{playerName}")
    public ResponseVO getPlayerInfo(@PathVariable String playerName) {
        if(playerName != null){
            List<? extends PlayerMatchData> list = playerServiceFactory.findAllMatchesByPlayerName(playerName);
            logger.info("获取到的数据为{}",list);
            return getServerErrorResponseVO(list);
        }else {
            return getServerErrorResponseVO("没有该选手");
        }
    }
}
