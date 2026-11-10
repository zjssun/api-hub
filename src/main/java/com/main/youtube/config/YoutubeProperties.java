package com.main.youtube.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "youtube")
public class YoutubeProperties {

    /** yt-dlp 可执行文件路径，默认走 PATH */
    private String ytdlpPath = "yt-dlp";

    /** ffmpeg 可执行文件路径，默认走 PATH */
    private String ffmpegPath = "ffmpeg";

    /** 下载文件存放目录，每个 job 一个子目录 */
    private String outputDir = "./downloads";

    /** YouTube cookies 文件（Netscape 格式），为空则匿名解析 */
    private String cookiesFile = "";

    /** 接口鉴权 Key，请求头 X-Api-Key；为空则不校验（仅开发用） */
    private String apiKey = "";

    /** 最大并发下载数，超出排队 */
    private int maxConcurrent = 3;

    /** 已下载文件保留小时数，过期自动清理 */
    private int retentionHours = 24;

    /** resolve 解析超时时间（秒） */
    private int resolveTimeoutSeconds = 60;

    /** 单个下载任务超时时间（分钟） */
    private int downloadTimeoutMinutes = 60;

    /** 默认视频格式选择器 */
    private String defaultFormatSelector = "bv*+ba/b";

    /** 默认纯音频格式 */
    private String audioFormat = "mp3";
}
