package com.main.controller;

import com.main.entity.enums.PlayerEnum;
import io.netty.channel.ChannelOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MyScheduledTask {
    private static final Logger logger = LoggerFactory.getLogger(MyScheduledTask.class);
    @Scheduled(fixedRate = 100 * 1000)
    public void executeTask(){
        logger.info("定时任务执行中...");
        //创建 WebClient 对象
        WebClient client = WebClient.create();

        //创建一个 Flux 流，遍历所有的 PlayerEnum 枚举值
        Flux<Tuple2<PlayerEnum,String>> resultFlux = Flux.fromArray(PlayerEnum.values())
                .flatMap(playerEnum -> {
                    logger.info("准备获取{}的比赛数据", playerEnum.getName());
                    String url = "https://www.faceit.com/api/stats/v1/stats/time/users/"+playerEnum.getUuid()+"/games/cs2";
                    logger.info("请求地址: {}", url);
                    // 发送 GET 请求并处理响应
                    return client.get().uri(url).retrieve().bodyToMono(String.class)
                            .map(response->{
                                // 请求成功时记录日志并返回玩家和响应数据的元组
                                logger.info("成功获取 {} 的数据!", playerEnum.getName());
                                return Tuples.of(playerEnum, response);
                            })
                            .doOnError(error->{
                                logger.error("获取 {} 的数据失败: {}", playerEnum.getName(), error.getMessage());
                            })
                            .onErrorResume(error->{
                                logger.warn("跳过玩家 {} 由于错误: {}", playerEnum.getName(), error.getMessage());
                                return Mono.empty();
                            });
                });

        //将所有的结果收集到一个 Map 中
        Map<PlayerEnum, String> allPlayerHistories = null;
        try {
            allPlayerHistories = resultFlux.collectMap(Tuple2::getT1,Tuple2::getT2).block(Duration.ofSeconds(90));

        }catch (IllegalStateException e){
            logger.warn("没有成功获取到任何玩家数据，或Flux为空: {}", e.getMessage());
            allPlayerHistories = new ConcurrentHashMap<>();
        }catch (RuntimeException e) {
            logger.error("获取玩家数据时发生错误或超时: {}", e.getMessage(), e);
            allPlayerHistories = new ConcurrentHashMap<>();
        }

        // 打印 allPlayerHistories 的内容
        if (allPlayerHistories != null && !allPlayerHistories.isEmpty()) {
            logger.info("--- 开始打印获取到的所有玩家历史数据 (共 {} 条) ---", allPlayerHistories.size());
            allPlayerHistories.forEach((player, historyJson) -> {
                logger.info("玩家: {}, 历史数据: {}", player.getName(), historyJson);
            });
        }else{
            logger.warn("未能收集到任何玩家的比赛历史数据，或者 'allPlayerHistories' Map 为空或为 null。");
        }
    }
}
