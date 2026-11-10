package com.main.service;

import com.main.mappers.VisitorMapper;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

@Service
public class VisitorService {

    private static final Logger logger = LoggerFactory.getLogger(VisitorService.class);

    /** 合法 uid：6~64 位字母数字下划线（8 位数字 UID / UUID 都兼容） */
    private static final Pattern VALID_UID = Pattern.compile("^[A-Za-z0-9_-]{6,64}$");

    private final VisitorMapper visitorMapper;

    /** 异步落库，不阻塞请求线程 */
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public VisitorService(VisitorMapper visitorMapper) {
        this.visitorMapper = visitorMapper;
    }

    public void recordAsync(String uid, String ip) {
        if (uid == null || !VALID_UID.matcher(uid).matches()) {
            return;
        }
        executor.submit(() -> {
            try {
                visitorMapper.upsertVisit(uid, truncate(ip, 45), LocalDateTime.now());
            } catch (Exception e) {
                logger.warn("记录访客失败 uid={}: {}", uid, e.getMessage());
            }
        });
    }

    private String truncate(String s, int max) {
        return s == null ? null : s.length() <= max ? s : s.substring(0, max);
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
    }
}
