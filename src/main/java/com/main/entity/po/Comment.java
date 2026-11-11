package com.main.entity.po;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Comment implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String visitorUid;
    private String nickname;
    private String avatar;
    private String content;
    private String ip;
    private LocalDateTime createdAt;

    /** 父评论 id，0 = 顶层评论，>0 = 回复（仅支持一级） */
    private Long parentId;

    /** 回复列表（非数据库字段，仅查询组装用） */
    private java.util.List<Comment> replies;
}
