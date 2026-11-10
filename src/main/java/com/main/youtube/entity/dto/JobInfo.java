package com.main.youtube.entity.dto;

import com.main.youtube.entity.enums.JobStatus;
import lombok.Data;

@Data
public class JobInfo {

    private String jobId;
    private JobStatus status;

    /** 0-100，总大小未知时为 -1 */
    private Double percent;

    private Long downloadedBytes;
    private Long totalBytes;

    /** 字节/秒 */
    private Long speedBps;

    /** 秒 */
    private Long etaSeconds;

    private String fileName;
    private Long fileSize;
    private String errorMessage;

    private String createdAt;
    private String finishedAt;
}
