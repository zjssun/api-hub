package com.main.youtube.service;

import com.main.youtube.config.YoutubeProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class YtDlpCommandBuilder {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(YtDlpCommandBuilder.class);

    private final YoutubeProperties properties;
    private volatile Boolean cookiesLogged;

    public YtDlpCommandBuilder(YoutubeProperties properties) {
        this.properties = properties;
    }

    /** 通用前缀参数：yt-dlp [--ffmpeg-location x] [--cookies x] --no-playlist --no-warnings */
    public List<String> base() {
        List<String> cmd = new ArrayList<>();
        cmd.add(properties.getYtdlpPath());

        if (StringUtils.hasText(properties.getFfmpegPath())) {
            cmd.add("--ffmpeg-location");
            cmd.add(properties.getFfmpegPath());
        }

        String cookiesFile = properties.getCookiesFile();
        if (StringUtils.hasText(cookiesFile)) {
            if (Files.isReadable(Path.of(cookiesFile))) {
                cmd.add("--cookies");
                cmd.add(cookiesFile);
                if (cookiesLogged == null) {
                    cookiesLogged = true;
                    logger.info("yt-dlp cookies 已启用: {}", cookiesFile);
                }
            } else if (cookiesLogged == null) {
                // 只告警一次，避免每个任务刷日志
                cookiesLogged = true;
                logger.warn("已配置 youtube.cookies-file 但文件不存在或不可读，将匿名执行: {}", cookiesFile);
            }
        }

        String proxy = properties.getProxy();
        if (StringUtils.hasText(proxy)) {
            cmd.add("--proxy");
            // yt-dlp 需要带 scheme 的形式
            cmd.add(proxy.contains("://") ? proxy : "http://" + proxy);
        }

        // 提取器参数（player_client、po_token 等），应对 YouTube 风控变化
        if (StringUtils.hasText(properties.getExtractorArgs())) {
            cmd.add("--extractor-args");
            cmd.add(properties.getExtractorArgs());
        }

        cmd.add("--no-playlist");
        cmd.add("--no-warnings");
        return cmd;
    }
}
