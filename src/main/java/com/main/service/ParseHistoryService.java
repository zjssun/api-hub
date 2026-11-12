package com.main.service;

import com.main.entity.po.ParseHistory;
import com.main.mappers.ParseHistoryMapper;
import com.main.youtube.entity.dto.FormatOption;
import com.main.youtube.entity.dto.VideoInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class ParseHistoryService {

    private static final Logger logger = LoggerFactory.getLogger(ParseHistoryService.class);
    private static final int KEEP_PER_SUBJECT = 50;

    private final ParseHistoryMapper mapper;

    public ParseHistoryService(ParseHistoryMapper mapper) {
        this.mapper = mapper;
    }

    /** 身份主体：登录 u:{userId}，游客 v:{visitorUid}；都无则 null */
    public static String subject(Long userId, String visitorUid) {
        if (userId != null) {
            return "u:" + userId;
        }
        return StringUtils.hasText(visitorUid) ? "v:" + visitorUid.trim() : null;
    }

    /** 解析成功后入库；失败只记日志不影响主流程 */
    public void record(String subject, VideoInfo info) {
        if (!StringUtils.hasText(subject) || info == null || !StringUtils.hasText(info.getId())) {
            return;
        }
        try {
            List<FormatOption> formats = info.getFormats() == null ? Collections.emptyList() : info.getFormats();
            int maxHeight = 0;
            long maxSize = 0;
            for (FormatOption f : formats) {
                if (f.getHeight() != null) maxHeight = Math.max(maxHeight, f.getHeight());
                Long size = f.getFilesize() != null ? f.getFilesize() : f.getFilesizeApprox();
                if (size != null) maxSize = Math.max(maxSize, size);
            }
            String tag = maxHeight >= 1440 ? "4K" : maxHeight >= 720 ? maxHeight + "P" : "标清";

            ParseHistory h = new ParseHistory();
            h.setSubject(subject);
            h.setVideoId(info.getId());
            h.setTitle(info.getTitle());
            h.setPlatform(info.getPlatform());
            h.setThumb(info.getThumbnailProxyUrl());
            h.setTag(tag);
            h.setTagType(tagType(tag));
            h.setSizeText(formatBytes(maxSize));
            h.setWebpageUrl(info.getWebpageUrl() != null ? info.getWebpageUrl()
                    : "https://www.youtube.com/watch?v=" + info.getId());
            h.setCreatedAt(LocalDateTime.now());

            mapper.deleteBySubjectAndVideo(subject, info.getId());
            mapper.insert(h);
            mapper.trim(subject, KEEP_PER_SUBJECT);
        } catch (Exception e) {
            logger.warn("解析历史入库失败 {}: {}", info.getId(), e.getMessage());
        }
    }

    public List<ParseHistory> list(String subject, int limit) {
        if (!StringUtils.hasText(subject)) {
            return Collections.emptyList();
        }
        return mapper.findLatest(subject, Math.min(Math.max(limit, 1), 50));
    }

    /** 与前端 index.js tagType() 一致 */
    private static String tagType(String tag) {
        if (tag == null) return "sd";
        if (tag.toUpperCase().contains("MP3")) return "mp3";
        if (tag.contains("4K") || tag.contains("2K") || tag.contains("2160") || tag.contains("1440")) return "4k";
        if (tag.contains("1080") || tag.contains("720")) return "hd";
        return "sd";
    }

    /** 与前端 utils/format.js formatBytes() 一致 */
    private static String formatBytes(long bytes) {
        if (bytes <= 0) return "";
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int i = 0;
        double v = bytes;
        while (v >= 1024 && i < units.length - 1) {
            v /= 1024;
            i++;
        }
        String num = v >= 100 ? String.valueOf(Math.round(v)) : String.format("%.1f", v);
        return num + " " + units[i];
    }
}
