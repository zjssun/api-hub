package com.main.service;

import com.main.entity.po.Comment;
import com.main.exception.BusinessException;
import com.main.mappers.CommentMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    private static final String CLASSPATH_BANWORD = "vocabulary/banword.txt";
    private static final String DEFAULT_NICKNAME = "访客体验者";
    private static final Set<String> ALLOWED_AVATARS = Set.of("cat", "totoro", "fox", "raccoon", "duck");

    private final CommentMapper commentMapper;
    private final String banwordFile;
    private final int maxLength;
    private final int rateLimitSeconds;

    private volatile Set<String> banwords = Set.of();
    private volatile long banwordFileMtime = -1;
    private final Map<String, Long> lastPostTime = new ConcurrentHashMap<>();

    public CommentService(CommentMapper commentMapper,
                          @Value("${comment.banword-file:}") String banwordFile,
                          @Value("${comment.max-length:500}") int maxLength,
                          @Value("${comment.rate-limit-seconds:10}") int rateLimitSeconds) {
        this.commentMapper = commentMapper;
        this.banwordFile = banwordFile;
        this.maxLength = maxLength;
        this.rateLimitSeconds = rateLimitSeconds;
    }

    @PostConstruct
    public void loadBanwords() {
        try (BufferedReader reader = openBanwordReader()) {
            if (reader == null) {
                logger.warn("敏感词库未找到 (comment.banword-file={})，跳过敏感词过滤", banwordFile);
                return;
            }
            Set<String> words = new HashSet<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String word = line.trim();
                if (word.isEmpty() || word.startsWith("#")) {
                    continue;
                }
                words.add(word.toLowerCase(Locale.ROOT));
            }
            banwords = words;
            banwordFileMtime = externalFileMtime();
            logger.info("敏感词库加载完成，共 {} 条", words.size());
        } catch (Exception e) {
            logger.warn("敏感词库加载失败，跳过敏感词过滤: {}", e.getMessage());
        }
    }

    /** 外部词库文件变更后每分钟自动热加载（内置 classpath 词库不变化，无需检查） */
    @Scheduled(fixedDelay = 60_000)
    public void reloadBanwordsIfChanged() {
        if (!StringUtils.hasText(banwordFile)) {
            return;
        }
        long mtime = externalFileMtime();
        if (mtime > 0 && mtime != banwordFileMtime) {
            logger.info("检测到敏感词库变更，重新加载: {}", banwordFile);
            loadBanwords();
        }
    }

    private long externalFileMtime() {
        try {
            if (StringUtils.hasText(banwordFile)) {
                return Files.getLastModifiedTime(Path.of(banwordFile)).toMillis();
            }
        } catch (Exception ignored) {
        }
        return -1;
    }

    private BufferedReader openBanwordReader() throws Exception {
        if (StringUtils.hasText(banwordFile)) {
            Path path = Path.of(banwordFile);
            if (Files.exists(path)) {
                return Files.newBufferedReader(path, StandardCharsets.UTF_8);
            }
            return null;
        }
        ClassPathResource resource = new ClassPathResource(CLASSPATH_BANWORD);
        if (!resource.exists()) {
            return null;
        }
        return new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
    }

    public boolean containsBanword(String text) {
        if (!StringUtils.hasText(text) || banwords.isEmpty()) {
            return false;
        }
        String lower = text.toLowerCase(Locale.ROOT);
        for (String word : banwords) {
            if (lower.contains(word)) {
                return true;
            }
        }
        return false;
    }

    public List<Comment> list(int limit) {
        return commentMapper.findLatest(Math.min(Math.max(limit, 1), 200));
    }

    public Comment add(String uid, String rawNickname, String avatar, String rawContent, String ip) {
        String content = rawContent == null ? "" : rawContent.trim();
        if (!StringUtils.hasText(content)) {
            throw new BusinessException("评论内容不能为空");
        }
        if (content.length() > maxLength) {
            throw new BusinessException("评论内容过长，最多 " + maxLength + " 字");
        }

        if (containsBanword(content) || containsBanword(rawNickname)) {
            throw new BusinessException("评论内容不宜发布");
        }

        if (StringUtils.hasText(uid)) {
            Long last = lastPostTime.get(uid);
            long now = System.currentTimeMillis();
            if (last != null && now - last < rateLimitSeconds * 1000L) {
                throw new BusinessException("发言太频繁，请稍后再试");
            }
            lastPostTime.put(uid, now);
        }

        String nickname = StringUtils.hasText(rawNickname) ? rawNickname.trim() : DEFAULT_NICKNAME;
        if (nickname.length() > 32) {
            nickname = nickname.substring(0, 32);
        }
        String safeAvatar = ALLOWED_AVATARS.contains(avatar) ? avatar : null;

        Comment comment = new Comment();
        comment.setVisitorUid(uid);
        comment.setNickname(nickname);
        comment.setAvatar(safeAvatar);
        comment.setContent(content);
        comment.setIp(ip);
        comment.setCreatedAt(LocalDateTime.now());
        commentMapper.insert(comment);

        logger.info("新评论 uid={}, nickname={}", uid, nickname);
        return comment;
    }
}
