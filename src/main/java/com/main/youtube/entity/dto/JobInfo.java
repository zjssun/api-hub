package com.main.youtube.entity.dto;

import com.main.youtube.entity.enums.JobStatus;
import lombok.Data;

import java.util.List;

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

    /** 多视频/播放列表任务的全部产物文件名（按下载顺序）；单文件为 null，走 /file?index=i 逐个取 */
    private List<String> fileNames;

    private String errorMessage;

    private String createdAt;
    private String finishedAt;
}
