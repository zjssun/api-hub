package com.main.service;

import com.github.houbb.sensitive.word.api.IWordDeny;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义敏感词源：外部文件（comment.banword-file / COMMENT_BANWORD_FILE）。
 * <p>
 * sensitive-word 在每次 init() 时调用 deny() 重建词库，
 * 因此文件变更后只需重新 init() 即可热生效。
 */
@Component
public class BanWordDeny implements IWordDeny {

    private static final Logger logger = LoggerFactory.getLogger(BanWordDeny.class);

    private final String banwordFile;

    public BanWordDeny(@Value("${comment.banword-file:}") String banwordFile) {
        this.banwordFile = banwordFile;
    }

    @Override
    public List<String> deny() {
        List<String> words = new ArrayList<>();
        if (!hasExternalFile()) {
            logger.warn("敏感词库未配置或文件不存在 (comment.banword-file={})，仅使用系统内置词库", banwordFile);
            return words;
        }
        try (BufferedReader reader = Files.newBufferedReader(Path.of(banwordFile), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String word = line.trim();
                if (!word.isEmpty() && !word.startsWith("#")) {
                    words.add(word);
                }
            }
            logger.info("自定义敏感词库加载 {} 条 ({})", words.size(), banwordFile);
        } catch (Exception e) {
            logger.warn("自定义敏感词库读取失败，仅使用系统内置词库: {}", e.getMessage());
        }
        return words;
    }

    /** 外部词库文件 mtime，用于热加载判断；文件不存在返回 -1 */
    public long externalFileMtime() {
        try {
            if (hasExternalFile()) {
                return Files.getLastModifiedTime(Path.of(banwordFile)).toMillis();
            }
        } catch (Exception ignored) {
        }
        return -1;
    }

    public boolean hasExternalFile() {
        return StringUtils.hasText(banwordFile) && Files.exists(Path.of(banwordFile));
    }
}
