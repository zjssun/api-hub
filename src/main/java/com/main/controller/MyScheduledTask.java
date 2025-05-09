package com.main.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.main.entity.enums.DateTimePatternEnum;
import com.main.entity.enums.PlayerEnum;
import com.main.entity.po.*;
import com.main.service.*;
import com.main.service.impl.DonkServiceImpl;
import com.main.utils.MatchTools;
import io.netty.channel.ChannelOption;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import reactor.util.retry.Retry;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MyScheduledTask {
    @Resource
    DonkService donkService;

    @Resource
    EligeService eligeService;

    @Resource
    ImService imService;

    @Resource
    JameService jameService;

    @Resource
    JksService jksService;

    @Resource
    JlService jlService;

    @Resource
    M0nesyService m0nesyService;

    @Resource
    NikoService nikoService;

    @Resource
    RopzService ropzService;

    @Resource
    S1mpleService s1mpleService;

    @Resource
    TwistzzService twistzzService;

    @Resource
    UnknownService unknownService;

    @Resource
    W0nderfulService w0nderfulService;

    @Resource
    ZywooService zywooService;



    private static final Logger logger = LoggerFactory.getLogger(MyScheduledTask.class);
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //创建 WebClient 对象
    private final WebClient client = WebClient.create();
    private final WebClient RoomClient = WebClient.create();
    private final WebClient DetailClient = WebClient.create();

    @Scheduled(fixedRate = 100 * 1000)
    public void executeTask(){
        logger.info("定时任务执行中...");
        //创建一个 PlayerMatchData对象的流
        Flux<PlayerMatchData> resultFlux = Flux.fromArray(PlayerEnum.values())
                .flatMap(playerEnum -> {
                    logger.info("准备获取{}的比赛数据", playerEnum.getName());
                    String url = "https://www.faceit.com/api/stats/v1/stats/time/users/"+playerEnum.getUuid()+"/games/cs2?page=0&size=1&game_mode=5v5";
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
                    switch (data) {
                        case M0nesy m0nesy -> {
                            try {
                                m0nesyService.add(m0nesy);
                            } catch (Exception e) {
                                logger.info("M0nesy已存在数据库！{}", data);
                            }
                        }
                        case Donk donk -> {
                            try {
                                donkService.add(donk);
                            } catch (Exception e) {
                                logger.info("donk已存在数据库！{}", data);
                            }
                        }
                        case Twistzz twistzz -> {
                            try {
                                twistzzService.add(twistzz);
                            } catch (Exception e) {
                                logger.info("Twistzz已存在数据库！{}", data);
                            }
                        }
                        case Elige elige -> {
                            try {
                                eligeService.add(elige);
                            } catch (Exception e) {
                                logger.info("Elige已存在数据库！{}", data);
                            }
                        }
                        case Zywoo zywoo -> {
                            try {
                                zywooService.add(zywoo);
                            } catch (Exception e) {
                                logger.info("Zywoo已存在数据库！{}", data);
                            }
                        }
                        case Jks jks -> {
                            try {
                                jksService.add(jks);
                            } catch (Exception e) {
                                logger.info("Jks已存在数据库{}", data);
                            }
                        }
                        case Jl jl -> {
                            try {
                                jlService.add(jl);
                            } catch (Exception e) {
                                logger.info("Jl已存在数据库{}", data);
                            }
                        }
                        case S1mple s1mple -> {
                            try {
                                s1mpleService.add(s1mple);
                            } catch (Exception e) {
                                logger.info("S1mple已存在数据库{}", data);
                            }
                        }
                        case Im im -> {
                            try {
                                imService.add(im);
                            } catch (Exception e) {
                                logger.info("Im已存在数据库{}", data);
                            }
                        }
                        case Niko niko -> {
                            try {
                                nikoService.add(niko);
                            } catch (Exception e) {
                                logger.info("Niko已存在数据库！{}", data);
                            }
                        }
                        case Ropz ropz -> {
                            try {
                                ropzService.add(ropz);
                            } catch (Exception e) {
                                logger.info("Ropz已存在数据库！{}", data);
                            }
                        }
                        case Jame jame -> {
                            try {
                                jameService.add(jame);
                            } catch (Exception e) {
                                logger.info("Jame已存在数据库！{}", data);
                            }
                        }
                        case W0nderful w0nderful -> {
                            try {
                                w0nderfulService.add(w0nderful);
                            } catch (Exception e) {
                                logger.info("W0nderful已存在数据库{}", data);
                            }
                        }
                        case null, default -> {
                            try {
                                unknownService.add((Unknown) data);
                            } catch (Exception e) {
                                logger.info("Unknown已存在数据库！{}", data);
                            }
                        }
                    }
                }catch (Exception e){
                    logger.error("Error saving data for {} : {}", data.getClass().getSimpleName(), e.getMessage(), e);
                }
            }
        }
        logger.info("--- 所有玩家历史数据解析和转换完毕 ---");
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
        //Rating
        matchData.setRating(MatchTools.calculateRating(matchStat.getString("c2")));
        // 三杀 四杀 五杀
        matchData.setTripleKill(matchStat.getString("i14"));
        matchData.setQuadroKill(matchStat.getString("i15"));
        matchData.setPentaKill(matchStat.getString("i16"));

        //如果bestOf为2 获取 effectiveRanking 和 playerAdr
        if(Objects.equals("2",bestOf) && publicMatchId != null){
            String matchRoomUrl = "https://www.faceit.com/api/match/v2/match/" + publicMatchId;
            String matchDetailUrl = "https://www.faceit.com/api/stats/v1/stats/matches/" + publicMatchId;
            //获取effectiveRanking
            Mono<String> effectiveRankingMono = fethEffectiveRanking(matchRoomUrl)
                    .onErrorResume(e->{
                       logger.warn("获取比赛 {} 的 effectiveRanking 失败: {}", publicMatchId, e.getMessage());
                       return Mono.just("unknown");
                    });

            //获取ADR
            Mono<String> playerAdrMono = fetchPlayAdr(matchDetailUrl, playerEnum.getUuid(), teamId)
                    .onErrorResume(e->{
                        logger.warn("获取比赛 {} 的 ADR 失败: {}", publicMatchId, e.getMessage());
                        return Mono.just("unknown");
                    });
            //组合两个Mono
            return Mono.zip(effectiveRankingMono,playerAdrMono)
                    .map(tuple->{
                        matchData.setEffectiveRanking(tuple.getT1());
                        matchData.setAdr(tuple.getT2());
                        return matchData;
                    }).defaultIfEmpty(matchData).onErrorReturn(matchData);
        }else{
            matchData.setEffectiveRanking("unknown");
            matchData.setAdr("unknown");
            return Mono.just(matchData);
        }
    }

    //访问房间信息api获取对局均分
    private Mono<String> fethEffectiveRanking(String matchRoomUrl){
        logger.info("获取比赛房间：{}的均分 ", matchRoomUrl);
        return RoomClient.get().uri(matchRoomUrl).retrieve().bodyToMono(String.class).retryWhen(Retry.backoff(3,Duration.ofSeconds(2)))
                .map(jsonResponse->{
                    try {
                        JSONObject roomData = JSON.parseObject(jsonResponse);
                        String effectiveRanking = roomData.getJSONObject("payload")
                                .getJSONObject("entityCustom")
                                .getString("effectiveRanking");
                        if (effectiveRanking.contains(".")) {
                            effectiveRanking = effectiveRanking.substring(0, effectiveRanking.indexOf("."));
                        }
                        return effectiveRanking;
                    }catch (Exception e) {
                        logger.warn("解析 effectiveRanking 失败 for {}: {}", matchRoomUrl, e.getMessage());
                        return "unknow";
                    }
                }).doOnError(WebClientResponseException.class, e -> {
                    logger.warn("访问API {} 失败: Code:{} ERROR:{}", matchRoomUrl, e.getStatusCode(), e.getResponseBodyAsString());
                });
    }

    //访问房间对战结果api获取玩家adr
    private Mono<String> fetchPlayAdr(String matchDetailUrl, String targetPlayerId, String targetTeamId){
        logger.info("获取比赛详细：{} ", matchDetailUrl);
        return DetailClient.get().uri(matchDetailUrl).retrieve().bodyToMono(String.class).retryWhen(Retry.backoff(3,Duration.ofSeconds(2)))
                .map(jsonResponse->{
                    try {
                        JSONArray matchDetails = JSON.parseArray(jsonResponse);
                        JSONArray teamsArray = null;
                        //获取2个队伍保存到JSON数组
                        teamsArray = matchDetails.getJSONObject(0).getJSONArray("teams");
                        if (teamsArray == null) {
                            logger.warn("在 {} 的响应中未找到 'teams' 数组", matchDetailUrl);
                            throw new RuntimeException("Teams data not found in match detail");
                        }
                        for(int i=0;i<teamsArray.size();i++){
                            JSONObject team = teamsArray.getJSONObject(i);
                            String currentTeamId = team.getString("teamId");
                            if(Objects.equals(targetTeamId,currentTeamId)){
                                JSONArray players = team.getJSONArray("players");
                                for(int j=0;j<players.size();j++){
                                    JSONObject player = players.getJSONObject(j);
                                    if(Objects.equals(targetPlayerId,player.getString("playerId"))){
                                        String adr = player.getString("c10");
                                        if (adr.contains(".")) {
                                            adr = adr.substring(0, adr.indexOf("."));
                                        }
                                        return adr;
                                    }
                                }
                            }
                        }
                    }catch (Exception e) {
                        logger.warn("解析 playerAdr 失败 for {}: {}", matchDetailUrl, e.getMessage());
                        return "unknow";
                    }
                    return "unknow";
                }).doOnError(WebClientResponseException.class, e -> {
                    logger.warn("访问API {} 失败: Code:{} ERROR:{}", matchDetailUrl, e.getStatusCode(), e.getResponseBodyAsString());
                });
    }
}
