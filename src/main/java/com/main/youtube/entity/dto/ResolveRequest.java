package com.main.youtube.entity.dto;

import lombok.Data;

@Data
public class ResolveRequest {

    /** 视频 URL 或 11 位 videoId */
    private String query;
}
