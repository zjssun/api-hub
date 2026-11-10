package com.main.youtube.service;

import com.main.exception.BusinessException;
import com.main.youtube.config.YoutubeProperties;
import com.main.youtube.entity.DownloadJob;
import com.main.youtube.entity.dto.DownloadRequest;
import com.main.youtube.entity.dto.JobInfo;
import com.main.youtube.entity.enums.JobStatus;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
public class DownloadService {

    private static final Logger logger = LoggerFactory.getLogger(DownloadService.class);

    /** 进度输出行前缀（--progress-template 产生） */
    private static final String PROGRESS_PREFIX = "YTPROG|";

    /** 最终文件路径前缀（--print after_move 产生） */
    private static final String FILE_PREFIX = "YTFILE|";

    /** 兜底进度正则，匹配 "[download]  42.3% of ..." */
    private static final Pattern FALLBACK_PERCENT = Pattern.compile("\\[download]\\s+([\\d.]+)%");

    private static final Pattern ILLEGAL_FILENAME = Pattern.compile("[\\\\/:*?\"<>|\\p{Cntrl}]");

    private static final Set<String> ALLOWED_CONTAINERS = Set.of("mp4", "mkv", "webm");
    private static final Set<String> ALLOWED_AUDIO_FORMATS = Set.of("mp3", "m4a", "opus", "flac", "wav");

    private final YoutubeProperties properties;
    private final YtDlpCommandBuilder commandBuilder;
    private final YtDlpService ytDlpService;
    private final ProcessRunner processRunner;

    private final Map<String, DownloadJob> jobs = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final Semaphore concurrencyLimit;

    public DownloadService(YoutubeProperties properties, YtDlpCommandBuilder commandBuilder,
                           YtDlpService ytDlpService, ProcessRunner processRunner) {
        this.properties = properties;
        this.commandBuilder = commandBuilder;
        this.ytDlpService = ytDlpService;
        this.processRunner = processRunner;
        this.concurrencyLimit = new Semaphore(Math.max(1, properties.getMaxConcurrent()));
    }

    public JobInfo submit(DownloadRequest request) {
        String url = ytDlpService.normalizeUrl(request.getUrl());
        String jobId = UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        // 参数校验失败直接抛异常，不注册任务
        List<String> command = buildDownloadCommand(request, url, jobId);

        DownloadJob job = new DownloadJob();
        job.setJobId(jobId);
        jobs.put(jobId, job);
        executor.submit(() -> runJob(job, command));

        return job.toInfo();
    }

    public JobInfo get(String jobId) {
        return requireJob(jobId).toInfo();
    }

    public List<JobInfo> list() {
        return jobs.values().stream()
                .sorted(Comparator.comparing(DownloadJob::getCreatedAt).reversed())
                .map(DownloadJob::toInfo)
                .toList();
    }

    /** 返回任务产物文件路径，仅在 FINISHED 状态下可用 */
    public Path getJobFile(String jobId) {
        DownloadJob job = requireJob(jobId);
        if (job.getStatus() != JobStatus.FINISHED) {
            throw new BusinessException("任务尚未完成 (status=" + job.getStatus() + ")");
        }
        Path file = resolveJobFile(job);
        if (file == null) {
            throw new BusinessException("产物文件不存在或已被清理");
        }
        return file;
    }

    public JobInfo cancelOrDelete(String jobId) {
        DownloadJob job = requireJob(jobId);
        if (job.getStatus() == JobStatus.RUNNING || job.getStatus() == JobStatus.QUEUED) {
            job.setStatus(JobStatus.CANCELED);
            job.setFinishedAt(LocalDateTime.now());
            job.killProcess();
        } else {
            jobs.remove(jobId);
            deleteQuietly(jobDir(jobId));
        }
        return job.toInfo();
    }

    private void runJob(DownloadJob job, List<String> command) {
        boolean acquired = false;
        try {
            concurrencyLimit.acquire();
            acquired = true;

            if (job.getStatus() == JobStatus.CANCELED) {
                return;
            }
            job.setStatus(JobStatus.RUNNING);

            Path dir = jobDir(job.getJobId());
            Files.createDirectories(dir);

            int exitCode = processRunner.runStreaming(
                    command,
                    Duration.ofMinutes(properties.getDownloadTimeoutMinutes()),
                    line -> handleOutputLine(job, line),
                    job.getProcessRef()
            );

            if (job.getStatus() == JobStatus.CANCELED) {
                return;
            }

            if (exitCode == 0) {
                if (job.getFilePath() == null) {
                    job.setFilePath(findNewestFile(dir));
                }
                job.setStatus(JobStatus.FINISHED);
                job.setPercentHint(100.0);
            } else {
                job.setStatus(JobStatus.FAILED);
                job.setErrorMessage("yt-dlp 退出码 " + exitCode + ": " + job.tailAsString());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            job.setStatus(JobStatus.CANCELED);
        } catch (Exception e) {
            if (job.getStatus() != JobStatus.CANCELED) {
                job.setStatus(JobStatus.FAILED);
                job.setErrorMessage(e.getMessage());
                logger.error("下载任务 {} 失败", job.getJobId(), e);
            }
        } finally {
            if (job.getStatus() == JobStatus.RUNNING || job.getStatus() == JobStatus.QUEUED) {
                job.setStatus(JobStatus.FAILED);
            }
            if (job.getFinishedAt() == null) {
                job.setFinishedAt(LocalDateTime.now());
            }
            if (acquired) {
                concurrencyLimit.release();
            }
        }
    }

    private void handleOutputLine(DownloadJob job, String line) {
        job.appendOutput(line);

        if (line.startsWith(PROGRESS_PREFIX)) {
            // YTPROG|status|downloaded|total|total_est|speed|eta
            String[] parts = line.substring(PROGRESS_PREFIX.length()).split("\\|", -1);
            if (parts.length >= 6) {
                job.setDownloadedBytes(parseLong(parts[1]));
                Long total = parseLong(parts[2]);
                job.setTotalBytes(total != null ? total : parseLong(parts[3]));
                job.setSpeedBps(parseLong(parts[4]));
                job.setEtaSeconds(parseLong(parts[5]));
                if ("finished".equals(parts[0])) {
                    job.setPercentHint(100.0);
                }
            }
            return;
        }

        if (line.startsWith(FILE_PREFIX)) {
            job.setFilePath(Path.of(line.substring(FILE_PREFIX.length()).trim()));
            return;
        }

        Matcher m = FALLBACK_PERCENT.matcher(line);
        if (m.find()) {
            job.setPercentHint(Double.parseDouble(m.group(1)));
        }
    }

    private List<String> buildDownloadCommand(DownloadRequest request, String url, String jobId) {
        List<String> cmd = new ArrayList<>(commandBuilder.base());
        Path dir = jobDir(jobId);

        // 进度模板：状态|已下载|总大小|估算总大小|速度|ETA
        cmd.add("--newline");
        cmd.add("--progress-template");
        cmd.add("download:" + PROGRESS_PREFIX
                + "%(progress.status)s|%(progress.downloaded_bytes)s|%(progress.total_bytes)s"
                + "|%(progress.total_bytes_estimate)s|%(progress.speed)s|%(progress.eta)s");

        // 完成后输出最终文件路径
        cmd.add("--print");
        cmd.add("after_move:" + FILE_PREFIX + "%(filepath)s");

        cmd.add("--windows-filenames");

        if (Boolean.TRUE.equals(request.getAudioOnly())) {
            String audioFormat = StringUtils.hasText(request.getAudioFormat())
                    ? request.getAudioFormat().toLowerCase()
                    : properties.getAudioFormat();
            if (!ALLOWED_AUDIO_FORMATS.contains(audioFormat)) {
                throw new BusinessException("不支持的音频格式: " + audioFormat);
            }
            cmd.add("-f");
            cmd.add(StringUtils.hasText(request.getFormatSelector()) ? request.getFormatSelector() : "ba/b");
            cmd.add("-x");
            cmd.add("--audio-format");
            cmd.add(audioFormat);
        } else {
            cmd.add("-f");
            cmd.add(resolveFormatSelector(request));
            String container = StringUtils.hasText(request.getContainer())
                    ? request.getContainer().toLowerCase()
                    : "mp4";
            if (ALLOWED_CONTAINERS.contains(container)) {
                cmd.add("--merge-output-format");
                cmd.add(container);
            }
            if (Boolean.TRUE.equals(request.getEmbedSubs())) {
                cmd.add("--write-subs");
                cmd.add("--write-auto-subs");
                cmd.add("--sub-langs");
                cmd.add("all");
                cmd.add("--embed-subs");
            }
        }

        if (!Boolean.FALSE.equals(request.getEmbedMetadata())) {
            cmd.add("--embed-metadata");
        }
        if (!Boolean.FALSE.equals(request.getEmbedThumbnail())) {
            cmd.add("--embed-thumbnail");
        }

        cmd.add("-o");
        cmd.add(buildOutputTemplate(dir, request.getFileName()));
        cmd.add(url);
        return cmd;
    }

    private String resolveFormatSelector(DownloadRequest request) {
        if (StringUtils.hasText(request.getFormatSelector())) {
            return request.getFormatSelector();
        }
        Integer quality = request.getQuality();
        if (quality != null && quality > 0) {
            return "bv*[height<=" + quality + "]+ba/b[height<=" + quality + "]/b";
        }
        return properties.getDefaultFormatSelector();
    }

    private String buildOutputTemplate(Path dir, String fileName) {
        if (StringUtils.hasText(fileName)) {
            String safe = ILLEGAL_FILENAME.matcher(fileName.trim()).replaceAll("_");
            return dir.resolve(safe + ".%(ext)s").toString();
        }
        return dir.resolve("%(title).150B [%(id)s].%(ext)s").toString();
    }

    private Path jobDir(String jobId) {
        return Path.of(properties.getOutputDir()).resolve(jobId);
    }

    private DownloadJob requireJob(String jobId) {
        DownloadJob job = jobs.get(jobId);
        if (job == null) {
            throw new BusinessException("任务不存在: " + jobId);
        }
        return job;
    }

    private Path resolveJobFile(DownloadJob job) {
        Path file = job.getFilePath();
        if (file != null && Files.exists(file)) {
            return file;
        }
        return findNewestFile(jobDir(job.getJobId()));
    }

    private Path findNewestFile(Path dir) {
        if (!Files.isDirectory(dir)) {
            return null;
        }
        try (Stream<Path> stream = Files.list(dir)) {
            return stream
                    .filter(Files::isRegularFile)
                    .max(Comparator.comparing(p -> p.toFile().lastModified()))
                    .orElse(null);
        } catch (IOException e) {
            return null;
        }
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank() || "NA".equalsIgnoreCase(value)) {
            return null;
        }
        try {
            return (long) Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void deleteQuietly(Path dir) {
        if (dir == null || !Files.exists(dir)) {
            return;
        }
        try (Stream<Path> stream = Files.walk(dir)) {
            stream.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException ignored) {
                }
            });
        } catch (IOException e) {
            logger.warn("清理目录失败 {}: {}", dir, e.getMessage());
        }
    }

    /** 定时清理过期任务目录和任务记录 */
    @Scheduled(fixedDelay = 3600_000)
    public void cleanupExpired() {
        LocalDateTime deadline = LocalDateTime.now().minusHours(properties.getRetentionHours());
        Path baseDir = Path.of(properties.getOutputDir());
        if (!Files.isDirectory(baseDir)) {
            return;
        }

        int removed = 0;
        for (DownloadJob job : jobs.values()) {
            boolean expired = job.getFinishedAt() != null && job.getFinishedAt().isBefore(deadline);
            boolean stale = job.getCreatedAt().isBefore(deadline)
                    && job.getStatus() != JobStatus.RUNNING && job.getStatus() != JobStatus.QUEUED;
            if (expired || stale) {
                jobs.remove(job.getJobId());
                deleteQuietly(jobDir(job.getJobId()));
                removed++;
            }
        }

        // 清理 map 中已无记录但磁盘上残留的目录（服务重启后）
        try (Stream<Path> stream = Files.list(baseDir)) {
            for (Path dir : stream.filter(Files::isDirectory).toList()) {
                String name = dir.getFileName().toString();
                if ("_thumbs".equals(name)) {
                    continue;
                }
                boolean known = jobs.containsKey(name);
                boolean old = Files.getLastModifiedTime(dir).toInstant()
                        .isBefore(java.time.Instant.now().minus(Duration.ofHours(properties.getRetentionHours())));
                if (!known && old) {
                    deleteQuietly(dir);
                    removed++;
                }
            }
        } catch (IOException e) {
            logger.warn("清理过期下载目录失败: {}", e.getMessage());
        }

        if (removed > 0) {
            logger.info("清理了 {} 个过期下载任务目录", removed);
        }
    }

    @PreDestroy
    public void shutdown() {
        for (DownloadJob job : jobs.values()) {
            if (job.getStatus() == JobStatus.RUNNING || job.getStatus() == JobStatus.QUEUED) {
                job.killProcess();
            }
        }
        executor.shutdown();
    }
}
