package com.main.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.main.constant.Constants;
import com.main.entity.enums.PlayerEnum;
import com.main.entity.po.*;
import com.main.service.*;
import com.main.utils.MatchTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

@Component
@ConditionalOnProperty(name = "scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class MyScheduledTask {
    @Autowired
    private PlayerServiceFactory playerServiceFactory;

    private static final Logger logger = LoggerFactory.getLogger(MyScheduledTask.class);
    private static final SimpleDateFormat SDF = new SimpleDateFormat("MMM-dd-yyyy HH:mm:ss", Locale.ENGLISH);
    //创建 WebClient 对象
    private final WebClient client = WebClient.create();
    private final WebClient RoomClient = WebClient.create();
    private final WebClient DetailClient = WebClient.create();

    @Scheduled(fixedRateString = "${scheduler.time}")
    public void executeTask(){
        logger.info("定时任务执行中...");

        //创建一个 PlayerMatchData对象的流
        Flux<PlayerMatchData> resultFlux = Flux.fromArray(PlayerEnum.values())
                .flatMap(playerEnum -> {
                    logger.info("准备获取{}的比赛数据", playerEnum.getName());
                    String url = "https://www.faceit.com/api/stats/v1/stats/time/users/"+playerEnum.getUuid()+"/games/cs2?page=0&size=3&game_mode=5v5";
                    logger.info("请求地址: {}", url);
                    // 发送 GET 请求并处理响应
                    return client.get().uri(url).retrieve().bodyToMono(String.class).retryWhen(Retry.backoff(3,Duration.ofSeconds(2)))
                            //处理Json数据
                            .flatMapMany(historyJsonString ->{
                                try {
                                    JSONArray matchesStatsArray = JSON.parseArray(historyJsonString);
                                    //如果数据为空
                                    if (matchesStatsArray == null || matchesStatsArray.isEmpty()) {
                                        logger.info("  玩家 {} 的比赛统计数据解析后为空数组.", playerEnum.getName());
                                        return Flux.<JSONObject>empty(); // No matches to process for this player
                                    }
                                    logger.info("  玩家 {} 共有 {} 条比赛统计记录.", playerEnum.getName(), matchesStatsArray.size());
                                    //将JSONArray转换为Flux<JSONObject>，以便单独处理每个匹配项
                                    return Flux.fromIterable(matchesStatsArray).cast(JSONObject.class);
                                }catch (JSONException e){
                                    logger.error("解析玩家 {} 的初始比赛历史JSON失败",playerEnum.getName());
                                    return Flux.<JSONObject>empty();
                                }
                            }).flatMap(matchStat-> {
                                try {
                                    return processSingleMatchStat(playerEnum, matchStat);
                                } catch (NoSuchMethodException e) {
                                    logger.info("执行{}的processSingleMatchStat失败！",playerEnum.getName());
                                    throw new RuntimeException(e);
                                }
                            });
                });


        //将所有的结果收集到一个 Map 中
        List<PlayerMatchData> allParsedMatches = null;
        try {
            allParsedMatches = resultFlux.collectList().block(Duration.ofSeconds(170));
        }catch (IllegalStateException e){
            logger.warn("没有成功获取到任何玩家数据，或Flux为空: {}", e.getMessage());
            allParsedMatches = new ArrayList<>();
        }

        //保存到数据库
        if (allParsedMatches != null && !allParsedMatches.isEmpty()) {
            logger.info("--- 开始保存玩家数据到数据库 (共 {} 条记录) ---", allParsedMatches.size());
            for (PlayerMatchData data : allParsedMatches) {
                try {
                    @SuppressWarnings("unchecked")
                    Class<? extends PlayerMatchData> dataClass = (Class<? extends PlayerMatchData>) data.getClass();
                    Optional<PlayerEnum> playerEnumOptional = PlayerEnum.fromDataClass(dataClass);
                    if(playerEnumOptional.isPresent()){
                        playerServiceFactory.save(playerEnumOptional.get(), data);
                    }else {
                        logger.error("无法为类 {} 找到对应的 PlayerEnum 配置。", data.getClass().getName());
                    }
                }catch (Exception e){
                    logger.error("Error saving data for {} : {}", data.getClass().getSimpleName(), e.getMessage(), e);
                }
            }
        }
        logger.info("--- 所有玩家历史数据解析和转换完毕 ---");
        //删除过期数据
        delExpireData();
    }

    //保存JSON数据到对应的类
    private Mono<PlayerMatchData> processSingleMatchStat(PlayerEnum playerEnum,JSONObject matchStat) throws NoSuchMethodException {
        if(matchStat==null){
            return Mono.empty();
        }
        //实例接口类
        PlayerMatchData matchData;
        //实例玩家类
        try {
            matchData = playerEnum.getDataClass().getDeclaredConstructor().newInstance();
        }catch (Exception  e){
            logger.error("创建玩家 {} (类: {}) 的数据对象实例失败: {}", playerEnum.getName(), playerEnum.getDataClass().getName(), e.getMessage(), e);
            return Mono.empty();
        }
        Long createdAtTimestamp = matchStat.getLong("created_at");
        if (createdAtTimestamp != null) {
            matchData.setTime(SDF.format(new Date(createdAtTimestamp)));
            matchData.setTimestamp(String.valueOf(createdAtTimestamp));
        }
        //NickName
        matchData.setNickName(matchStat.getString("nickname"));
        //所属队伍id
        String teamId = matchStat.getString("teamId");
        matchData.setTeam(teamId);
        //比赛地图
        matchData.setMatchMap(matchStat.getString("i1"));
        //比分 （格式：11/13）
        String rawMatchScore = matchStat.getString("i18");
        matchData.setMatchScore(rawMatchScore);
        //玩家队伍比分
        String playerTeamFinalScoreStr = matchStat.getString("c5");
        //比赛结果 win loss draw
        matchData.setMatchResult(MatchTools.calculateMatchResult(rawMatchScore,playerTeamFinalScoreStr));
        //比赛房间ID
        String publicMatchId = matchStat.getString("matchId");
        matchData.setMatchId(publicMatchId);
        //比赛房间链接
        matchData.setRoomUrl("https://www.faceit.com/en/cs2/room/" + matchStat.getString("matchId") +"/scoreboard");
        String bestOf = matchStat.getString("bestOf");
        matchData.setBestOf(bestOf);
        // 击杀 死亡 助攻
        matchData.setTotalKills(matchStat.getString("i6"));
        matchData.setTotalDeaths(matchStat.getString("i8"));
        matchData.setTotalAssistsl(matchStat.getString("i7"));
        //Rating adr
        matchData.setRating(MatchTools.calculateRating(matchStat.getString("c2")));
        matchData.setAdr(matchStat.getString("c10"));
        // 三杀 四杀 五杀
        matchData.setTripleKill(matchStat.getString("i14"));
        matchData.setQuadroKill(matchStat.getString("i15"));
        matchData.setPentaKill(matchStat.getString("i16"));

        //如果bestOf为2 获取 effectiveRanking 和 playerAdr
        if(Objects.equals("1",bestOf) && publicMatchId != null){
            String matchRoomUrl = "https://www.faceit.com/api/match/v2/match/" + publicMatchId;
            //获取effectiveRanking
            Mono<String> effectiveRankingMono = fethEffectiveRanking(matchRoomUrl)
                    .onErrorResume(e->{
                       logger.warn("获取比赛 {} 的 effectiveRanking 失败: {}", publicMatchId, e.getMessage());
                       return Mono.just("unknown");
                    });
            return effectiveRankingMono.flatMap(effectiveRanking->{
                matchData.setEffectiveRanking(effectiveRanking);
                return Mono.just(matchData);
            });
        }else{
            matchData.setEffectiveRanking("unknown");
            return Mono.just(matchData);
        }
    }

    //访问房间信息api获取对局均分
    private Mono<String> fethEffectiveRanking(String matchRoomUrl){
        return RoomClient.get().uri(matchRoomUrl).retrieve().bodyToMono(String.class).retryWhen(Retry.backoff(3,Duration.ofSeconds(2)))
                .map(jsonResponse->{
                    try {
                        JSONObject roomData = JSON.parseObject(jsonResponse);
                        String effectiveRanking = roomData.getJSONObject("payload")
                                .getJSONObject("entityCustom")
                                .getString("effectiveRanking");
                        if(effectiveRanking == null){
                            return "unknow";
                        }else {
                            if (effectiveRanking.contains(".")) {
                                return effectiveRanking.substring(0, effectiveRanking.indexOf("."));
                            }else
                                return effectiveRanking;
                        }
                    }catch (Exception e) {
                        logger.warn("解析 effectiveRanking 失败 for {}: {}", matchRoomUrl, e.getMessage());
                        return "unknown";
                    }
                }).doOnError(WebClientResponseException.class, e -> {
                    logger.warn("访问API {} 失败: Code:{} ERROR:{}", matchRoomUrl, e.getStatusCode(), e.getResponseBodyAsString());
                });
    }

    //删除过期数据
    private void delExpireData(){
        logger.info("正在检查过期数据...");
        for(PlayerEnum playerEnum : PlayerEnum.values()){
           int deletedCount = playerServiceFactory.deleteOutdatedData(playerEnum,Constants.TWO_MONTH_TIME);
           if(deletedCount>0){
               logger.info("删除了玩家{}的{}条过期数据",playerEnum.getName(),deletedCount);
           }
        }
        logger.info("检查完毕!");
    }
}
