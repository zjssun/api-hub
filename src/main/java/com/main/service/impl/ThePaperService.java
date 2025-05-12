package com.main.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.main.constant.Constants;
import com.main.entity.po.ThePaper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class ThePaperService {
    private static final Logger logger = LoggerFactory.getLogger(ThePaperService.class);
    private final WebClient webClient = WebClient.create();

    public List<ThePaper> fetchHotNews(){
        List<ThePaper> paperList = new ArrayList<>();
        logger.info("正在获取澎湃热点数据...");
        try{
            Mono<String> responseMono = webClient.get().uri(Constants.THE_PAPER_URL).retrieve().bodyToMono(String.class);
            String jsonResponse = responseMono.block();
            if(jsonResponse != null && !jsonResponse.isEmpty()){
                JSONObject rootObject = JSON.parseObject(jsonResponse);
                JSONObject dataObject = rootObject.getJSONObject("data");
                if(dataObject != null){
                    JSONArray hotNewsArray = dataObject.getJSONArray("hotNews");
                    if(hotNewsArray != null){
                        int itemsToFetch = Math.min(hotNewsArray.size(),10);
                        for(int i=0;i<itemsToFetch;i++){
                            JSONObject newsItem = hotNewsArray.getJSONObject(i);
                            if(newsItem != null){
                                ThePaper paper = new ThePaper();
                                paper.setcoutId(newsItem.getString("contId"));
                                paper.setTitle(newsItem.getString("name"));
                                paper.setUrl(Constants.THE_PAPER_ARTICLE_URL + newsItem.getString("contId"));
                                paperList.add(paper);
                            }
                        }
                    }
                }
            }else {
                logger.info("获取澎湃热点数据失败: 响应为空");
            }
        }catch (Exception e){
            logger.error("获取澎湃热点失败: {}", e.getMessage(), e);
        }
        logger.info("获取澎湃热点数据完成!");
        return paperList;
    }
}
