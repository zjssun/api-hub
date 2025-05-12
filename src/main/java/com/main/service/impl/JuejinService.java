package com.main.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.main.constant.Constants;
import com.main.entity.po.Juejin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class JuejinService {
    private static final Logger logger = LoggerFactory.getLogger(JuejinService.class);
    private final WebClient webClient = WebClient.create();

    public List<Juejin> getJuejinData(){
        List<Juejin> juejinList = new ArrayList<>();
        logger.info("正在获取掘金数据...");
        try{
            Mono<String> responseMono = webClient.get().uri(Constants.JUEJIN_URL).retrieve().bodyToMono(String.class);
            String jsonResponse = responseMono.block();
            if(jsonResponse != null && !jsonResponse.isEmpty()){
                JSONObject jsonObject = JSON.parseObject(jsonResponse);
                JSONArray dataArray = jsonObject.getJSONArray("data");
                if(dataArray!=null){
                    int itemsToFetch = Math.min(dataArray.size(), 10);
                    for(int i=0;i<itemsToFetch;i++){
                        JSONObject dataItem = dataArray.getJSONObject(i);
                        JSONObject itemContent = dataItem.getJSONObject("content");
                        Juejin juejin = new Juejin();
                        juejin.setContent_id(itemContent.getString("content_id"));
                        juejin.setTitle(itemContent.getString("title"));
                        juejin.setUrl(Constants.JUEJIN_ARTICLE_URL+itemContent.getString("content_id"));
                        juejinList.add(juejin);
                    }
                }
            }else {
                logger.info("访问掘金api成功但是响应为空!");
            }
            return juejinList;
        }catch (Exception e){
            logger.error("获取掘金数据失败: {}", e.getMessage(), e);
        }
        return juejinList;
    }
}
