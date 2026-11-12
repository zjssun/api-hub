package com.main.controller;

import com.main.entity.vo.ResponseVO;
import com.main.service.AuthService;
import com.main.service.ParseHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/history")
public class ParseHistoryController extends ABaseController {

    private final ParseHistoryService historyService;
    private final AuthService authService;

    public ParseHistoryController(ParseHistoryService historyService, AuthService authService) {
        this.historyService = historyService;
        this.authService = authService;
    }

    /** 当前主体的最近解析记录（登录按账号、游客按访客标识） */
    @GetMapping
    public ResponseVO list(@RequestParam(defaultValue = "20") int limit, HttpServletRequest request) {
        Long userId = authService.resolveToken(request.getHeader("X-Auth-Token"));
        String subject = ParseHistoryService.subject(userId, request.getHeader("X-Visitor-Id"));
        return getSuccessResponseVO(historyService.list(subject, limit));
    }
}
