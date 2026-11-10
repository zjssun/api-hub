package com.main.youtube.entity.dto;

import lombok.Data;

@Data
public class DownloadRequest {

    /** 视频 URL 或 11 位 videoId，必填 */
    private String url;

    /**
     * yt-dlp 格式选择器，如 "bv*[height<=1080]+ba/b[height<=1080]"。
     * 为空时用 quality 组装；两者都空用服务端默认。
     */
    private String formatSelector;

    /** 画质上限：1080/720/480/360，0 或不传 = 最高 */
    private Integer quality;

    /** 合并输出容器：mp4/mkv/webm，默认 mp4 */
    private String container;

    /** 只下载音频（配合 audioFormat 转码） */
    private Boolean audioOnly;

    /** 音频转码格式：mp3/m4a/opus，默认取配置 */
    private String audioFormat;

    /** 内嵌全部字幕（仅视频容器支持） */
    private Boolean embedSubs;

    /** 内嵌元数据/封面，默认 true */
    private Boolean embedMetadata;
    private Boolean embedThumbnail;

    /** 自定义文件名（不含扩展名），为空用 标题 [id] */
    private String fileName;
}
