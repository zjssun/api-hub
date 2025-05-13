package com.main.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.main.constant.Constants;
import com.main.entity.po.Tskr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TskrService {
    private static final Logger logger = LoggerFactory.getLogger(TskrService.class);
    private final WebClient webClient = WebClient.create();

    public List<Tskr> fetchTskrData(){
        List<Tskr> tskrList = new ArrayList<>();
        Map<String,Object> param = new HashMap<>();
        param.put("siteId",1);
        param.put("platformId",2);
        Map<String,Object> body = new HashMap<>();
        body.put("partner_id", "wap");
        body.put("param", param);
        body.put("timestamp", Instant.now().getEpochSecond());
        logger.info("正在获取36kr数据...");
        try{
            Mono<String> responseMono = webClient.post().uri(Constants.TSKR_URL).contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve().bodyToMono(String.class);
            String jsonResponse = responseMono.block();
            if(jsonResponse != null && !jsonResponse.isEmpty()){
                JSONObject jsonObject = JSON.parseObject(jsonResponse);
                JSONObject getdata = jsonObject.getJSONObject("data");
                JSONArray jsonArray = getdata.getJSONArray("hotRankList");
                int itemsToFetch = Math.min(jsonArray.size(), 20);
                for (int i=0;i<itemsToFetch;i++){
                    JSONObject dataItem = jsonArray.getJSONObject(i);
                    JSONObject templateMaterial = dataItem.getJSONObject("templateMaterial");
                    Tskr tskr = new Tskr();
                    tskr.setId(templateMaterial.getString("itemId"));
                    tskr.setTitle(templateMaterial.getString("widgetTitle"));
                    tskr.setUrl(Constants.TSKR_ARTICLE_URL+templateMaterial.getString("itemId"));
                    tskrList.add(tskr);
                }
                logger.info("获取36kr数据成功!");
            }else {
                logger.info("访问36kr api成功但是响应为空!data:{}",jsonResponse);
            }
        }catch (Exception e){
            logger.info("获取Tskr数据失败: {},body:{}", e.getMessage(),body);
        }
        return tskrList;
    }
}
