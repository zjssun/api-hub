package com.main.controller;

import com.main.entity.vo.ResponseVO;
import com.main.exception.BusinessException;
import com.main.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

/**
 * 评论功能已下架（UGC 合规要求）。
 * POST 一律返回"已下架"提示（旧版小程序前端原样展示）；
 * GET 返回空列表，存量评论数据与 CommentService 保留，随时可恢复。
 */
@RestController
@RequestMapping("/api/comments")
public class CommentController extends ABaseController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseVO list(@RequestParam(defaultValue = "50") int limit) {
        return getSuccessResponseVO(Collections.emptyList());
    }

    @PostMapping
    public ResponseVO add(@RequestBody Map<String, String> body, HttpServletRequest request) {
        throw new BusinessException("评论功能已下架");
    }
}
