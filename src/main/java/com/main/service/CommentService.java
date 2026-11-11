package com.main.service;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.main.entity.po.Comment;
import com.main.exception.BusinessException;
import com.main.mappers.CommentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    private static final String DEFAULT_NICKNAME = "访客体验者";
    private static final Set<String> ALLOWED_AVATARS = Set.of("cat", "totoro", "fox", "raccoon", "duck");

    private final CommentMapper commentMapper;
    private final SensitiveWordBs sensitiveWordBs;
    private final int maxLength;
    private final int rateLimitSeconds;

    private final Map<String, Long> lastPostTime = new ConcurrentHashMap<>();

    public CommentService(CommentMapper commentMapper,
                          SensitiveWordBs sensitiveWordBs,
                          @Value("${comment.max-length:500}") int maxLength,
                          @Value("${comment.rate-limit-seconds:10}") int rateLimitSeconds) {
        this.commentMapper = commentMapper;
        this.sensitiveWordBs = sensitiveWordBs;
        this.maxLength = maxLength;
        this.rateLimitSeconds = rateLimitSeconds;
    }

    public boolean containsBanword(String text) {
        return StringUtils.hasText(text) && sensitiveWordBs.contains(text);
    }

    public List<Comment> list(int limit) {
        List<Comment> top = commentMapper.findLatest(Math.min(Math.max(limit, 1), 200));
        if (top.isEmpty()) {
            return top;
        }
        List<Long> ids = top.stream().map(Comment::getId).toList();
        Map<Long, List<Comment>> grouped = commentMapper.findReplies(ids).stream()
                .collect(java.util.stream.Collectors.groupingBy(Comment::getParentId));
        for (Comment c : top) {
            c.setReplies(grouped.getOrDefault(c.getId(), List.of()));
        }
        return top;
    }

    public Comment add(String uid, String rawNickname, String avatar, String rawContent,
                       Long parentId, String ip) {
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

        long pid = parentId == null ? 0L : parentId;
        if (pid != 0L && commentMapper.countTopLevelById(pid) == 0) {
            throw new BusinessException("回复的评论不存在");
        }

        Comment comment = new Comment();
        comment.setVisitorUid(uid);
        comment.setNickname(nickname);
        comment.setAvatar(safeAvatar);
        comment.setContent(content);
        comment.setIp(ip);
        comment.setParentId(pid);
        comment.setCreatedAt(LocalDateTime.now());
        commentMapper.insert(comment);

        logger.info("新评论 uid={}, nickname={}", uid, nickname);
        return comment;
    }
}
