package com.main.youtube.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.exception.BusinessException;
import com.main.youtube.config.YoutubeProperties;
import com.main.youtube.entity.dto.FormatOption;
import com.main.youtube.entity.dto.ThumbnailInfo;
import com.main.youtube.entity.dto.VideoInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class YtDlpService {

    private static final Logger logger = LoggerFactory.getLogger(YtDlpService.class);
    private static final Pattern VIDEO_ID = Pattern.compile("^[A-Za-z0-9_-]{11}$");
    private static final Pattern INVALID_ID_CHARS = Pattern.compile("[^A-Za-z0-9_-]");

    /** 封面档位，按清晰度从高到低，404 时逐级回退 */
    private static final Map<String, List<String>> THUMB_FALLBACK = Map.of(
            "maxres", List.of("maxres", "sd", "hq"),
            "sd", List.of("sd", "hq"),
            "hq", List.of("hq"),
            "mq", List.of("mq", "hq"),
            "default", List.of("default", "hq")
    );

    private final YoutubeProperties properties;
    private final YtDlpCommandBuilder commandBuilder;
    private final ProcessRunner processRunner;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    public YtDlpService(YoutubeProperties properties, YtDlpCommandBuilder commandBuilder,
                        ProcessRunner processRunner) {
        this.properties = properties;
        this.commandBuilder = commandBuilder;
        this.processRunner = processRunner;
    }

    /** 把 11 位 videoId 规范化为完整 URL，其余输入原样交给 yt-dlp */
    public String normalizeUrl(String query) {
        if (query == null || query.isBlank()) {
            throw new BusinessException("query 不能为空");
        }
        String q = query.trim();
        if (VIDEO_ID.matcher(q).matches()) {
            return "https://www.youtube.com/watch?v=" + q;
        }
        return q;
    }

    /** 仅提取 URL 中的 videoId，提取不到返回 null */
    public String extractVideoId(String url) {
        if (VIDEO_ID.matcher(url).matches()) {
            return url;
        }
        var m = Pattern.compile("[?&]v=([A-Za-z0-9_-]{11})").matcher(url);
        if (m.find()) {
            return m.group(1);
        }
        m = Pattern.compile("(?:youtu\\.be/|shorts/|embed/|live/)([A-Za-z0-9_-]{11})").matcher(url);
        return m.find() ? m.group(1) : null;
    }

    public VideoInfo resolve(String query) {
        String url = normalizeUrl(query);

        List<String> cmd = new ArrayList<>(commandBuilder.base());
        cmd.add("--skip-download");
        cmd.add("-J");
        cmd.add(url);

        String json = processRunner.run(cmd, Duration.ofSeconds(properties.getResolveTimeoutSeconds()));
        return mapVideoInfo(json);
    }

    private VideoInfo mapVideoInfo(String json) {
        JsonNode root;
        try {
            root = objectMapper.readTree(json);
        } catch (Exception e) {
            throw new BusinessException("yt-dlp 输出解析失败", e);
        }

        if (root.has("_type") && !"video".equals(root.get("_type").asText())) {
            throw new BusinessException("该链接不是单个视频（当前仅支持视频解析）");
        }

        VideoInfo info = new VideoInfo();
        info.setId(text(root, "id"));
        info.setTitle(text(root, "title"));
        info.setWebpageUrl(text(root, "webpage_url"));
        info.setUploader(text(root, "uploader", "channel"));
        info.setUploaderUrl(text(root, "uploader_url", "channel_url"));
        info.setChannelId(text(root, "channel_id"));
        info.setDurationSeconds(longOrNull(root, "duration"));
        info.setViewCount(longOrNull(root, "view_count"));
        info.setLikeCount(longOrNull(root, "like_count"));
        info.setDescription(text(root, "description"));
        info.setThumbnail(text(root, "thumbnail"));
        info.setUploadDate(formatUploadDate(text(root, "upload_date")));
        info.setThumbnailProxyUrl("/api/youtube/thumbnail/" + info.getId());

        List<ThumbnailInfo> thumbs = new ArrayList<>();
        for (JsonNode t : root.path("thumbnails")) {
            String turl = text(t, "url");
            if (turl == null) {
                continue;
            }
            ThumbnailInfo ti = new ThumbnailInfo();
            ti.setUrl(turl);
            ti.setId(text(t, "id"));
            ti.setWidth(intOrNull(t, "width"));
            ti.setHeight(intOrNull(t, "height"));
            thumbs.add(ti);
        }
        info.setThumbnails(thumbs);

        List<FormatOption> formats = new ArrayList<>();
        for (JsonNode f : root.path("formats")) {
            String note = text(f, "format_note");
            String ext = text(f, "ext");
            // 跳过缩略图/故事板等非媒体格式
            if ("storyboard".equals(note) || "mhtml".equals(ext)) {
                continue;
            }
            FormatOption fo = new FormatOption();
            fo.setFormatId(text(f, "format_id"));
            fo.setExt(ext);
            fo.setHeight(intOrNull(f, "height"));
            fo.setWidth(intOrNull(f, "width"));
            fo.setFps(doubleOrNull(f, "fps"));
            fo.setFilesize(longOrNull(f, "filesize"));
            fo.setFilesizeApprox(longOrNull(f, "filesize_approx"));
            fo.setTbr(doubleOrNull(f, "tbr"));
            fo.setFormatNote(note);
            fo.setDynamicRange(text(f, "dynamic_range"));
            fo.setAudioLanguage(text(f, "language"));

            String vcodec = text(f, "vcodec");
            String acodec = text(f, "acodec");
            fo.setVcodec(vcodec);
            fo.setAcodec(acodec);
            boolean noVideo = vcodec == null || "none".equals(vcodec);
            boolean noAudio = acodec == null || "none".equals(acodec);
            fo.setAudioOnly(noVideo && !noAudio);
            fo.setVideoOnly(!noVideo && noAudio);
            fo.setMuxed(!noVideo && !noAudio);
            formats.add(fo);
        }
        formats.sort(Comparator.comparing(f -> f.getTbr() == null ? 0.0 : f.getTbr()));
        info.setFormats(formats);

        return info;
    }

    /**
     * 代理拉取视频封面，带磁盘缓存。
     *
     * @param res maxres/sd/hq/mq/default
     * @return 图片字节，找不到返回 null
     */
    public byte[] fetchThumbnail(String videoId, String res) {
        if (!VIDEO_ID.matcher(videoId).matches() || INVALID_ID_CHARS.matcher(videoId).find()) {
            throw new BusinessException("非法 videoId");
        }
        String key = THUMB_FALLBACK.containsKey(res) ? res : "hq";
        Path cacheDir = Path.of(properties.getOutputDir(), "_thumbs");
        Path cacheFile = cacheDir.resolve(videoId + "_" + key + ".jpg");

        try {
            if (Files.exists(cacheFile)) {
                return Files.readAllBytes(cacheFile);
            }

            for (String level : THUMB_FALLBACK.get(key)) {
                String url = "https://i.ytimg.com/vi/" + videoId + "/" + level + "default.jpg";
                HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                        .timeout(Duration.ofSeconds(15))
                        .GET()
                        .build();
                HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
                if (response.statusCode() == 200 && response.body().length > 0) {
                    Files.createDirectories(cacheDir);
                    Files.write(cacheFile, response.body());
                    return response.body();
                }
            }
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            logger.warn("拉取封面失败 videoId={}, res={}: {}", videoId, res, e.getMessage());
        }
        return null;
    }

    private String formatUploadDate(String uploadDate) {
        if (StringUtils.hasText(uploadDate) && uploadDate.length() == 8) {
            return uploadDate.substring(0, 4) + "-" + uploadDate.substring(4, 6) + "-" + uploadDate.substring(6, 8);
        }
        return uploadDate;
    }

    private String text(JsonNode node, String... fields) {
        for (String field : fields) {
            JsonNode v = node.get(field);
            if (v != null && !v.isNull() && !v.asText().isBlank()) {
                return v.asText();
            }
        }
        return null;
    }

    private Long longOrNull(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return v != null && v.isNumber() ? v.asLong() : null;
    }

    private Integer intOrNull(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return v != null && v.isNumber() ? v.asInt() : null;
    }

    private Double doubleOrNull(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return v != null && v.isNumber() ? v.asDouble() : null;
    }
}
