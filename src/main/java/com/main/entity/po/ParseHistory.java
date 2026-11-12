package com.main.entity.po;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/** 解析历史：subject 为 u:{userId}（登录）或 v:{visitorUid}（游客），同一视频重复解析仅保留最新一条 */
@Data
public class ParseHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String subject;
    private String videoId;
    private String title;
    private String platform;
    /** 缩略图代理路径（/api/youtube/thumbnail?url=…），前端拼接域名后直出 */
    private String thumb;
    private String tag;
    private String tagType;
    private String sizeText;
    private String webpageUrl;
    private LocalDateTime createdAt;
}
