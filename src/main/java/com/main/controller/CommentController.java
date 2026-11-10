package com.main.controller;

import com.main.entity.vo.ResponseVO;
import com.main.exception.BusinessException;
import com.main.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentController extends ABaseController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /** 最新留言列表 */
    @GetMapping
    public ResponseVO list(@RequestParam(defaultValue = "50") int limit) {
        return getSuccessResponseVO(commentService.list(limit));
    }

    /** 发表评论：body {nickname, avatar, content}，作者身份取 X-Visitor-Id 头 */
    @PostMapping
    public ResponseVO add(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String uid = request.getHeader("X-Visitor-Id");
        if (!StringUtils.hasText(uid)) {
            throw new BusinessException("访客标识缺失");
        }
        var comment = commentService.add(
                uid.trim(),
                body.get("nickname"),
                body.get("avatar"),
                body.get("content"),
                clientIp(request)
        );
        return getSuccessResponseVO(comment);
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwarded)) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(realIp)) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }
}
