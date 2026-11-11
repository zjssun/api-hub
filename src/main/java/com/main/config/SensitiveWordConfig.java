package com.main.config;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.houbb.sensitive.word.support.allow.WordAllows;
import com.github.houbb.sensitive.word.support.deny.WordDenys;
import com.main.service.BanWordDeny;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 敏感词引擎 Bean：系统内置词库 + 自定义 banword.txt 合并。
 * 评论过滤、注册昵称校验等共用同一个实例。
 */
@Configuration
public class SensitiveWordConfig {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveWordConfig.class);

    private final BanWordDeny banWordDeny;
    private SensitiveWordBs sensitiveWordBs;
    private volatile long banwordFileMtime = -1;

    public SensitiveWordConfig(BanWordDeny banWordDeny) {
        this.banWordDeny = banWordDeny;
    }

    @Bean
    public SensitiveWordBs sensitiveWordBs() {
        sensitiveWordBs = SensitiveWordBs.newInstance()
                .wordDeny(WordDenys.chains(WordDenys.defaults(), banWordDeny))
                .wordAllow(WordAllows.defaults())
                .init();
        banwordFileMtime = banWordDeny.externalFileMtime();
        logger.info("敏感词引擎初始化完成");
        return sensitiveWordBs;
    }

    /** 外部词库文件变更后每分钟自动重建 DFA */
    @Scheduled(fixedDelay = 60_000)
    public void reloadBanwordsIfChanged() {
        if (!banWordDeny.hasExternalFile() || sensitiveWordBs == null) {
            return;
        }
        long mtime = banWordDeny.externalFileMtime();
        if (mtime > 0 && mtime != banwordFileMtime) {
            logger.info("检测到敏感词库变更，重新初始化引擎");
            // init 会重新调用 deny() 读取文件，完成后无缝切换新词库
            sensitiveWordBs.init();
            banwordFileMtime = mtime;
        }
    }
}
