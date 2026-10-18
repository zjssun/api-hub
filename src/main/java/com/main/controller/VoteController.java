package com.main.controller;

import com.main.entity.vo.ResponseVO;
import com.main.service.ProVoteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VoteController extends ABaseController {
    private final ProVoteService proVoteService;

    public VoteController(ProVoteService proVoteService) {
        this.proVoteService = proVoteService;
    }

    @PostMapping("/vote")
    public ResponseVO vote(@RequestParam String name) {
        if (proVoteService.vote(name)) {
            return getSuccessResponseVO("ok");
        }
        return getServerErrorResponseVO("无效的选手名字");
    }

    @GetMapping("/vote/stats")
    public ResponseVO stats() {
        return getSuccessResponseVO(proVoteService.stats());
    }
}
