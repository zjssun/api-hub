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

    /** 代理地址，如 127.0.0.1:7890 或 http://127.0.0.1:7890；为空直连 */
    private String proxy = "";

    /** 接口鉴权 Key，请求头 X-Api-Key；为空则不校验（仅开发用） */
    private String apiKey = "";

    /** 最大并发下载数，超出排队 */
    private int maxConcurrent = 3;

    /** 多视频/播放列表链接单次最多下载条目数（--playlist-end），防整列烧配额 */
    private int playlistMaxItems = 10;

    /** 已下载文件保留小时数，过期自动清理 */
    private int retentionHours = 24;

    /** resolve 解析超时时间（秒） */
    private int resolveTimeoutSeconds = 60;

    /** 单个下载任务超时时间（分钟） */
    private int downloadTimeoutMinutes = 60;

    /** 默认视频格式选择器（"最佳画质"档：avc1+m4a 优先产出可微信分享的 h264 mp4，零 avc1 降级原编码） */
    private String defaultFormatSelector = "bv*[vcodec^=avc1]+ba[acodec^=mp4a]/bv*[vcodec^=avc1]+ba/b[vcodec^=avc1]/bv*+ba/b";

    /** yt-dlp --extractor-args（如 youtube:player_client=... / po_token 注入），留空不传 */
    private String extractorArgs = "";

    /** 默认纯音频格式 */
    private String audioFormat = "mp3";

    /** 下载产物缓存：相同链接+参数复用已下文件（命中仍计当日配额） */
    private boolean cacheEnabled = true;

    /** 缓存目录总量上限（MB），超限按最旧先删 */
    private long cacheMaxMb = 20 * 1024;
}
