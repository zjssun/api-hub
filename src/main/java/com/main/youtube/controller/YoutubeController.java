package com.main.youtube.controller;

import com.main.controller.ABaseController;
import com.main.entity.vo.ResponseVO;
import com.main.exception.BusinessException;
import com.main.youtube.entity.dto.DownloadRequest;
import com.main.youtube.entity.dto.ResolveRequest;
import com.main.youtube.service.DownloadService;
import com.main.youtube.service.YtDlpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/youtube")
public class YoutubeController extends ABaseController {

    private static final Logger logger = LoggerFactory.getLogger(YoutubeController.class);

    private final YtDlpService ytDlpService;
    private final DownloadService downloadService;

    public YoutubeController(YtDlpService ytDlpService, DownloadService downloadService) {
        this.ytDlpService = ytDlpService;
        this.downloadService = downloadService;
    }

    /** 解析视频信息（标题/时长/封面/可用格式） */
    @PostMapping("/resolve")
    public ResponseVO resolve(@RequestBody ResolveRequest request) {
        return getSuccessResponseVO(ytDlpService.resolve(request.getQuery()));
    }

    /**
     * 代理视频封面（大陆小程序无法直连 i.ytimg.com）。
     * res: maxres | sd | hq | mq | default，默认 hq
     */
    @GetMapping("/thumbnail/{videoId}")
    public ResponseEntity<byte[]> thumbnail(@PathVariable String videoId,
                                            @RequestParam(defaultValue = "hq") String res) {
        byte[] image = ytDlpService.fetchThumbnail(videoId, res);
        if (image == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .cacheControl(org.springframework.http.CacheControl.maxAge(
                        java.time.Duration.ofDays(7)).cachePublic())
                .body(image);
    }

    /** 创建下载任务，返回 jobId 供轮询 */
    @PostMapping("/download")
    public ResponseVO download(@RequestBody DownloadRequest request) {
        return getSuccessResponseVO(downloadService.submit(request));
    }

    /** 任务列表（按创建时间倒序） */
    @GetMapping("/jobs")
    public ResponseVO jobs() {
        return getSuccessResponseVO(downloadService.list());
    }

    /** 任务进度轮询 */
    @GetMapping("/jobs/{jobId}")
    public ResponseVO job(@PathVariable String jobId) {
        return getSuccessResponseVO(downloadService.get(jobId));
    }

    /** 取回下载产物，支持 Range（小程序 video 组件拖动进度条需要） */
    @GetMapping("/jobs/{jobId}/file")
    public ResponseEntity<?> file(@PathVariable String jobId,
                                  @RequestHeader(value = HttpHeaders.RANGE, required = false) String rangeHeader) {
        Path file = downloadService.getJobFile(jobId);
        Resource resource = new FileSystemResource(file);
        String fileName = file.getFileName().toString();

        try {
            long contentLength = Files.size(file);
            String disposition = "attachment; filename*=UTF-8''"
                    + URLEncoder.encode(fileName, StandardCharsets.UTF_8);

            MediaType mediaType = guessMediaType(fileName);

            if (StringUtils.hasText(rangeHeader)) {
                List<HttpRange> ranges = HttpRange.parseRanges(rangeHeader);
                if (!ranges.isEmpty()) {
                    ResourceRegion region = ranges.get(0).toResourceRegion(resource);
                    return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                            .contentType(mediaType)
                            .header(HttpHeaders.CONTENT_DISPOSITION, disposition)
                            .body(region);
                }
            }

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .contentLength(contentLength)
                    .header(HttpHeaders.CONTENT_DISPOSITION, disposition)
                    .body(resource);
        } catch (Exception e) {
            logger.error("读取任务文件失败 jobId={}", jobId, e);
            throw new BusinessException("读取文件失败: " + e.getMessage());
        }
    }

    /** 运行中任务 = 取消；已完成任务 = 删除记录和文件 */
    @DeleteMapping("/jobs/{jobId}")
    public ResponseVO delete(@PathVariable String jobId) {
        return getSuccessResponseVO(downloadService.cancelOrDelete(jobId));
    }

    private MediaType guessMediaType(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".mp4") || lower.endsWith(".m4a")) {
            return MediaType.parseMediaType("video/mp4");
        }
        if (lower.endsWith(".webm")) {
            return MediaType.parseMediaType("video/webm");
        }
        if (lower.endsWith(".mkv")) {
            return MediaType.parseMediaType("video/x-matroska");
        }
        if (lower.endsWith(".mp3")) {
            return MediaType.parseMediaType("audio/mpeg");
        }
        if (lower.endsWith(".opus") || lower.endsWith(".ogg")) {
            return MediaType.parseMediaType("audio/ogg");
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
