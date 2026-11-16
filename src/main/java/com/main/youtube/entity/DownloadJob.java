package com.main.youtube.entity;

import com.main.youtube.entity.dto.JobInfo;
import com.main.youtube.entity.enums.JobStatus;
import lombok.Data;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@Data
public class DownloadJob {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss");
    private static final int ERROR_TAIL_SIZE = 30;

    private String jobId;
    private volatile JobStatus status = JobStatus.QUEUED;

    private volatile Long downloadedBytes;
    private volatile Long totalBytes;
    private volatile Long speedBps;
    private volatile Long etaSeconds;
    private volatile boolean totalEstimated;

    private volatile Path filePath;
    private volatile String errorMessage;

    /** 多视频/播放列表：每个完成条目经 after_move 打印入列（下载顺序），多于一个时打包 zip */
    private final List<Path> completedFiles = new ArrayList<>();

    /** 提交者身份（用于完成时配额计费）：登录用户 id 或游客 uid */
    private Long userId;
    private String visitorUid;

    /** 产物缓存键（url+有效参数的摘要），完成后产物迁入 downloads/_cache/{cacheKey}/ */
    private String cacheKey;

    private final LocalDateTime createdAt = LocalDateTime.now();
    private volatile LocalDateTime finishedAt;

    /** 进度条兜底（无法计算字节比时使用解析到的百分比） */
    private volatile Double percentHint;

    private final java.util.concurrent.atomic.AtomicReference<Process> processRef =
            new java.util.concurrent.atomic.AtomicReference<>();

    public void killProcess() {
        Process p = processRef.get();
        if (p != null) {
            p.descendants().forEach(ProcessHandle::destroyForcibly);
            p.destroyForcibly();
        }
    }

    /** 保留末尾输出，进程失败时作为错误信息 */
    private final Deque<String> outputTail = new ArrayDeque<>(ERROR_TAIL_SIZE + 8);

    public synchronized void appendOutput(String line) {
        if (outputTail.size() >= ERROR_TAIL_SIZE) {
            outputTail.pollFirst();
        }
        outputTail.addLast(line);
    }

    public synchronized String tailAsString() {
        return String.join("\n", outputTail);
    }

    public JobInfo toInfo() {
        JobInfo info = new JobInfo();
        info.setJobId(jobId);
        info.setStatus(status);
        info.setDownloadedBytes(downloadedBytes);
        info.setTotalBytes(totalBytes);
        info.setSpeedBps(speedBps);
        info.setEtaSeconds(etaSeconds);
        info.setErrorMessage(errorMessage);
        info.setCreatedAt(createdAt.format(FORMATTER));
        info.setFinishedAt(finishedAt == null ? null : finishedAt.format(FORMATTER));

        if (downloadedBytes != null && totalBytes != null && totalBytes > 0) {
            info.setPercent(Math.round(downloadedBytes * 1000.0 / totalBytes) / 10.0);
        } else {
            info.setPercent(percentHint != null ? percentHint : -1.0);
        }

        if (completedFiles.size() > 1) {
            info.setFileNames(completedFiles.stream()
                    .map(f -> f.getFileName().toString()).toList());
            Path first = completedFiles.get(0);
            info.setFileName(first.getFileName().toString());
            long sum = 0;
            boolean ok = false;
            for (Path f : completedFiles) {
                try {
                    sum += java.nio.file.Files.size(f);
                    ok = true;
                } catch (Exception ignored) {
                }
            }
            if (ok) {
                info.setFileSize(sum);
            }
        } else {
            Path p = filePath;
            if (p != null) {
                info.setFileName(p.getFileName().toString());
                try {
                    info.setFileSize(java.nio.file.Files.size(p));
                } catch (Exception ignored) {
                }
            }
        }
        return info;
    }
}
