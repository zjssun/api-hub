package com.main.controller;

import com.main.entity.po.PlayerMatch;
import com.main.entity.vo.ResponseVO;
import com.main.service.PlayerMatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/getmatch")
public class EptController extends ABaseController{
    @Autowired
    private PlayerMatchService playerMatchService;

    private static final Logger logger = LoggerFactory.getLogger(EptController.class);
    @GetMapping("/{playerName}")
    public ResponseVO getPlayerInfo(@PathVariable String playerName) {
        if(playerName != null){
            List<PlayerMatch> list = playerMatchService.findAllMatchesByPlayerName(playerName);
            return getSuccessResponseVO(list);
        }else {
            return getServerErrorResponseVO("没有该选手");
        }
    }
}
