package com.main.youtube.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class VideoInfo {

    private String id;
    private String title;
    private String webpageUrl;

    private String uploader;
    private String uploaderUrl;
    private String channelId;

    /** yyyy-MM-dd */
    private String uploadDate;

    private Long durationSeconds;
    private Long viewCount;
    private Long likeCount;

    private String description;

    /** 后端代理封面，小程序直接可用：/api/youtube/thumbnail/{videoId} */
    private String thumbnailProxyUrl;

    /** 原始封面地址（在大陆不可直连，仅备用） */
    private String thumbnail;

    private List<ThumbnailInfo> thumbnails;
    private List<FormatOption> formats;
}
