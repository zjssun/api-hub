package com.main.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.main.constant.Constants;
import com.main.entity.enums.PlayerEnum;
import com.main.entity.po.PlayerMatch;
import com.main.service.PlayerMatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@ConditionalOnProperty(name = "scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class MyScheduledTask {
    private static final Logger logger = LoggerFactory.getLogger(MyScheduledTask.class);
    private static final Duration API_TIMEOUT = Duration.ofSeconds(15);
    private static final Duration RETRY_DELAY = Duration.ofSeconds(2);
    private static final int PLAYER_REQUEST_CONCURRENCY = 2;
    private static final int MATCH_REQUEST_CONCURRENCY = 2;
    private static final String FACEIT_USER_AGENT = "api-hub/1.0";
    private static final String DEFAULT_FACEIT_BASE_URL = "https://open.faceit.com/data/v4";
    private static final String UNKNOWN = "unknown";

    private final PlayerMatchService playerMatchService;
    private final FaceitMatchParser faceitMatchParser;
    private final WebClient faceitClient;
    private final String faceitApiBaseUrl;
    private final String faceitApiToken;
    private final int historyLimit;

    public MyScheduledTask(
            PlayerMatchService playerMatchService,
            @Value("${faceit.api.base-url:https://open.faceit.com/data/v4}") String faceitApiBaseUrl,
            @Value("${faceit.api.token:}") String faceitApiToken,
            @Value("${faceit.api.history-limit:2}") int historyLimit) {
        this.playerMatchService = playerMatchService;
        this.faceitMatchParser = new FaceitMatchParser();
        this.faceitClient = WebClient.builder()
                .defaultHeader(HttpHeaders.USER_AGENT, FACEIT_USER_AGENT)
                .build();
        this.faceitApiBaseUrl = normalizeBaseUrl(faceitApiBaseUrl);
        this.faceitApiToken = normalize(faceitApiToken);
        this.historyLimit = Math.max(1, historyLimit);
    }

    private record PendingPlayerMatch(PlayerEnum playerEnum, PlayerMatch match) {
    }

    @Scheduled(fixedRateString = "${scheduler.time}")
    public void executeTask() {
        logger.info("定时任务执行中...");
        if (faceitApiToken.isBlank()) {
            logger.error("FACEIT API token 未配置，跳过本轮定时任务。请设置 `faceit.api.token` 或环境变量 `FACEIT_API_TOKEN`。");
            return;
        }

        Flux<PendingPlayerMatch> resultFlux = Flux.fromArray(PlayerEnum.values())
                .flatMapSequential(this::fetchPlayerMatches, PLAYER_REQUEST_CONCURRENCY, 1);

        List<PendingPlayerMatch> allParsedMatches;
        try {
            allParsedMatches = resultFlux.collectList().block(Duration.ofSeconds(170));
        } catch (Exception e) {
            logger.warn("定时任务执行失败，未能收集完整玩家数据: {}", buildErrorMessage(e), e);
            allParsedMatches = new ArrayList<>();
        }

        if (allParsedMatches != null && !allParsedMatches.isEmpty()) {
            logger.info("--- 开始保存玩家数据到数据库 (共 {} 条记录) ---", allParsedMatches.size());
            for (PendingPlayerMatch parsedMatch : allParsedMatches) {
                try {
                    playerMatchService.save(parsedMatch.playerEnum(), parsedMatch.match());
                } catch (Exception e) {
                    logger.error("保存玩家 {} 的比赛数据失败: {}", parsedMatch.playerEnum().getName(), e.getMessage(), e);
                }
            }
        }

        logger.info("--- 所有玩家历史数据解析和转换完毕 ---");
        delExpireData();
    }

    private Flux<PendingPlayerMatch> fetchPlayerMatches(PlayerEnum playerEnum) {
        String historyUrl = buildHistoryUrl(playerEnum);
        logger.info("准备获取 {} 的比赛数据, 请求地址: {}", playerEnum.getName(), historyUrl);
        return fetchBody(historyUrl, "玩家 " + playerEnum.getName() + " 历史数据")
                .flatMapMany(historyJsonString -> parseHistoryResponse(playerEnum, historyJsonString))
                .flatMapSequential(historyMatch -> processSingleMatch(playerEnum, historyMatch)
                                .map(match -> new PendingPlayerMatch(playerEnum, match)),
                        MATCH_REQUEST_CONCURRENCY,
                        1)
                .onErrorResume(e -> {
                    logger.warn("跳过玩家 {} 的本轮抓取，原因: {}", playerEnum.getName(), buildErrorMessage(e));
                    return Flux.empty();
                });
    }

    private Flux<FaceitMatchParser.HistoryMatchContext> parseHistoryResponse(PlayerEnum playerEnum, String historyJsonString) {
        try {
            JSONObject historyResponse = JSON.parseObject(historyJsonString);
            JSONArray items = historyResponse.getJSONArray("items");
            if (items == null || items.isEmpty()) {
                logger.info("玩家 {} 的比赛历史为空.", playerEnum.getName());
                return Flux.empty();
            }

            logger.info("玩家 {} 共有 {} 条比赛历史记录.", playerEnum.getName(), items.size());
            return Flux.fromIterable(items)
                    .cast(JSONObject.class)
                    .concatMap(historyItem -> {
                        Optional<FaceitMatchParser.HistoryMatchContext> parsedMatch = faceitMatchParser.parseHistoryMatch(playerEnum, historyItem);
                        if (parsedMatch.isEmpty()) {
                            logger.warn("跳过玩家 {} 的比赛记录，无法从历史接口中定位玩家数据: {}", playerEnum.getName(), abbreviate(historyItem.toJSONString()));
                            return Flux.empty();
                        }
                        return Flux.just(parsedMatch.get());
                    });
        } catch (JSONException e) {
            logger.error("解析玩家 {} 的比赛历史 JSON 失败，响应内容: {}", playerEnum.getName(), abbreviate(historyJsonString), e);
            return Flux.empty();
        }
    }

    private Mono<PlayerMatch> processSingleMatch(PlayerEnum playerEnum, FaceitMatchParser.HistoryMatchContext historyMatch) {
        String matchId = historyMatch.match().getMatchId();
        String matchStatsUrl = buildMatchStatsUrl(matchId);
        return fetchBody(matchStatsUrl, "比赛 " + matchId + " stats")
                .flatMap(matchStatsJsonString -> applyMatchStats(playerEnum, historyMatch, matchStatsJsonString))
                .flatMap(match -> populateEffectiveRanking(historyMatch, match))
                .onErrorResume(e -> {
                    logger.warn("跳过玩家 {} 的比赛 {}，原因: {}", playerEnum.getName(), matchId, buildErrorMessage(e));
                    return Mono.empty();
                });
    }

    private Mono<PlayerMatch> applyMatchStats(
            PlayerEnum playerEnum,
            FaceitMatchParser.HistoryMatchContext historyMatch,
            String matchStatsJsonString) {
        try {
            JSONObject matchStatsResponse = JSON.parseObject(matchStatsJsonString);
            Optional<PlayerMatch> parsedMatch = faceitMatchParser.applyMatchStats(playerEnum, historyMatch, matchStatsResponse);
            if (parsedMatch.isEmpty()) {
                logger.warn("比赛 {} 的 stats 数据里没有找到玩家 {}，本场跳过。", historyMatch.match().getMatchId(), playerEnum.getName());
                return Mono.empty();
            }
            return Mono.just(parsedMatch.get());
        } catch (JSONException e) {
            logger.error("解析比赛 {} 的 stats JSON 失败，响应内容: {}", historyMatch.match().getMatchId(), abbreviate(matchStatsJsonString), e);
            return Mono.empty();
        }
    }

    private Mono<PlayerMatch> populateEffectiveRanking(
            FaceitMatchParser.HistoryMatchContext historyMatch,
            PlayerMatch playerMatch) {
        String matchDetailUrl = buildMatchDetailUrl(playerMatch.getMatchId());
        return fetchBody(matchDetailUrl, "比赛 " + playerMatch.getMatchId() + " 详情")
                .map(matchDetailJsonString -> parseEffectiveRanking(matchDetailJsonString, historyMatch.teamKey(), playerMatch.getMatchId()))
                .onErrorResume(e -> {
                    logger.warn("获取比赛 {} 的 effectiveRanking 失败: {}", playerMatch.getMatchId(), buildErrorMessage(e));
                    return Mono.just(UNKNOWN);
                })
                .map(effectiveRanking -> {
                    playerMatch.setEffectiveRanking(effectiveRanking);
                    return playerMatch;
                });
    }

    private String parseEffectiveRanking(String matchDetailJsonString, String teamKey, String matchId) {
        try {
            JSONObject matchDetailResponse = JSON.parseObject(matchDetailJsonString);
            return faceitMatchParser.parseEffectiveRanking(matchDetailResponse, teamKey);
        } catch (JSONException e) {
            logger.error("解析比赛 {} 的详情 JSON 失败，响应内容: {}", matchId, abbreviate(matchDetailJsonString), e);
            return UNKNOWN;
        }
    }

    private Mono<String> fetchBody(String url, String requestName) {
        return faceitClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, buildAuthorizationHeader())
                .retrieve()
                .bodyToMono(String.class)
                .timeout(API_TIMEOUT)
                .retryWhen(Retry.backoff(3, RETRY_DELAY)
                        .filter(this::shouldRetry)
                        .doBeforeRetry(retrySignal -> logger.warn(
                                "{} 请求重试第 {} 次, url={}, reason={}",
                                requestName,
                                retrySignal.totalRetriesInARow() + 1,
                                url,
                                buildErrorMessage(retrySignal.failure()))))
                .doOnError(WebClientResponseException.class, e -> logger.warn(
                        "{} 请求失败, url={}, status={}, body={}",
                        requestName,
                        url,
                        e.getStatusCode().value(),
                        abbreviate(e.getResponseBodyAsString())))
                .doOnError(e -> {
                    if (!(e instanceof WebClientResponseException)) {
                        logger.warn(
                                "{} 请求异常, url={}, type={}, message={}",
                                requestName,
                                url,
                                e.getClass().getSimpleName(),
                                abbreviate(e.getMessage()));
                    }
                });
    }

    private boolean shouldRetry(Throwable throwable) {
        if (throwable instanceof WebClientResponseException webClientResponseException) {
            int statusCode = webClientResponseException.getStatusCode().value();
            return statusCode == 429 || webClientResponseException.getStatusCode().is5xxServerError();
        }
        return true;
    }

    private String buildHistoryUrl(PlayerEnum playerEnum) {
        return faceitApiBaseUrl + "/players/" + playerEnum.getUuid() + "/history?limit=" + historyLimit;
    }

    private String buildMatchStatsUrl(String matchId) {
        return faceitApiBaseUrl + "/matches/" + matchId + "/stats";
    }

    private String buildMatchDetailUrl(String matchId) {
        return faceitApiBaseUrl + "/matches/" + matchId;
    }

    private String buildAuthorizationHeader() {
        if (faceitApiToken.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return faceitApiToken;
        }
        return "Bearer " + faceitApiToken;
    }

    private String normalizeBaseUrl(String baseUrl) {
        String normalizedBaseUrl = normalize(baseUrl);
        if (normalizedBaseUrl.isBlank()) {
            return DEFAULT_FACEIT_BASE_URL;
        }
        if (normalizedBaseUrl.endsWith("/")) {
            return normalizedBaseUrl.substring(0, normalizedBaseUrl.length() - 1);
        }
        return normalizedBaseUrl;
    }

    private String normalize(String text) {
        return text == null ? "" : text.trim();
    }

    private String buildErrorMessage(Throwable throwable) {
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause.getClass().getSimpleName() + ": " + abbreviate(rootCause.getMessage());
    }

    private String abbreviate(String text) {
        if (text == null || text.isBlank()) {
            return "<empty>";
        }
        String normalized = text.replaceAll("\\s+", " ").trim();
        if (normalized.length() <= 300) {
            return normalized;
        }
        return normalized.substring(0, 300) + "...";
    }

    private void delExpireData() {
        logger.info("正在检查过期数据...");
        for (PlayerEnum playerEnum : PlayerEnum.values()) {
            int deletedCount = playerMatchService.deleteOutdatedData(playerEnum, Constants.TWO_MONTH_TIME);
            if (deletedCount > 0) {
                logger.info("删除了玩家 {} 的 {} 条过期数据", playerEnum.getName(), deletedCount);
            }
        }
        logger.info("检查完毕!");
    }
}
