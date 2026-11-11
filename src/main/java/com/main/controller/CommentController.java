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
    private final com.main.service.AuthService authService;

    public CommentController(CommentService commentService, com.main.service.AuthService authService) {
        this.commentService = commentService;
        this.authService = authService;
    }

    /** 最新留言列表 */
    @GetMapping
    public ResponseVO list(@RequestParam(defaultValue = "50") int limit) {
        return getSuccessResponseVO(commentService.list(limit));
    }

    /** 发表评论：需登录（X-Auth-Token），作者身份记为账号 id */
    @PostMapping
    public ResponseVO add(@RequestBody Map<String, String> body, HttpServletRequest request) {
        Long userId = authService.resolveToken(request.getHeader("X-Auth-Token"));
        if (userId == null) {
            throw new BusinessException("请先登录后发表评论");
        }
        String uid = "u" + userId;
        Long parentId = null;
        try {
            String pid = body.get("parentId");
            if (StringUtils.hasText(pid)) {
                parentId = Long.parseLong(pid.trim());
            }
        } catch (NumberFormatException e) {
            throw new BusinessException("parentId 不合法");
        }

        var comment = commentService.add(
                uid.trim(),
                body.get("nickname"),
                body.get("avatar"),
                body.get("content"),
                parentId,
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
