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

    private final YoutubeProperties properties;

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
        if (StringUtils.hasText(cookiesFile) && Files.exists(Path.of(cookiesFile))) {
            cmd.add("--cookies");
            cmd.add(cookiesFile);
        }

        String proxy = properties.getProxy();
        if (StringUtils.hasText(proxy)) {
            cmd.add("--proxy");
            // yt-dlp 需要带 scheme 的形式
            cmd.add(proxy.contains("://") ? proxy : "http://" + proxy);
        }

        cmd.add("--no-playlist");
        cmd.add("--no-warnings");
        return cmd;
    }
}
