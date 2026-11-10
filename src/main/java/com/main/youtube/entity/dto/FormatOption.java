package com.main.youtube.entity.dto;

import lombok.Data;

@Data
public class FormatOption {

    private String formatId;
    private String ext;
    private Integer height;
    private Integer width;
    private Double fps;

    /** 纯音频流（无视频轨） */
    private Boolean audioOnly;

    /** 纯视频流（无音频轨） */
    private Boolean videoOnly;

    /** 视频+音频混流 */
    private Boolean muxed;

    private String vcodec;
    private String acodec;

    /** 精确大小（字节），可能为空 */
    private Long filesize;

    /** 估算大小（字节），可能为空 */
    private Long filesizeApprox;

    /** 总码率 kbps */
    private Double tbr;

    private String formatNote;
    private String dynamicRange;
    private String audioLanguage;
}
