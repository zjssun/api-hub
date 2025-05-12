package com.main.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.main.constant.Constants;
import com.main.entity.po.Douban;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class DoubanService {
    private static final Logger logger = LoggerFactory.getLogger(DoubanService.class);
    private final WebClient webClient = WebClient.create();

    public List<Douban> fecthDoubanData(){
        List<Douban> doubanList = new ArrayList<>();
        logger.info("正在获取豆瓣数据...");
        try{
            Mono<String> responseMono = webClient.get().uri(Constants.DouBan_URL)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36")
                    .header("Referer", "https://movie.douban.com/tv/")
                    .header("Origin", "https://movie.douban.com")
                    .header("Cookie", "ll=\"118290\"; bid=aS51m5ezzLs; __utmc=30149280; _vwo_uuid_v2=DD312E3F0B86322452256DA4F01ED87DE|cfbd6617b4e44048a255489896ad776e; Hm_lvt_6d4a8cfea88fa457c3127e14fb5fabc2=1740492093; Hm_lpvt_6d4a8cfea88fa457c3127e14fb5fabc2=1740492093; HMACCOUNT=700C09E15B151087; _ga=GA1.2.1200859574.1740492093; _ga_Y4GN1R87RG=GS1.1.1740492092.1.0.1740492095.0.0.0; push_doumail_num=0; __utmv=30149280.19025; push_noty_num=0; frodotk=\"c4a3a39c6df194d04deaead549d4be70\"; talionusr=\"eyJpZCI6ICIxOTAyNTMxNjYiLCAibmFtZSI6ICJcdThkNzdcdTRlMmFcdTk4Y2VcdTlhOWFcdTc2ODRcdTU0MGRcdTViNTcifQ==\"; __utma=30149280.1903755155.1739608257.1747051224.1747053405.34; __utmz=30149280.1747053405.34.32.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); dbcl2=\"190253166:LXc4Aqr2oc0\"; ck=eNh9; frodotk_db=\"2ae0ed82587be0ec80a2c8870973a2c4\"")
                    .retrieve().bodyToMono(String.class);
            String jsonResponse = responseMono.block();
            if(jsonResponse != null && !jsonResponse.isEmpty()){
                JSONObject jsonObject = JSON.parseObject(jsonResponse);
                JSONArray dataArray = jsonObject.getJSONArray("items");
                int itemsToFetch = Math.min(dataArray.size(), 20);
                for(int i=0;i<itemsToFetch;i++){
                    JSONObject dataItem = dataArray.getJSONObject(i);
                    Douban douban = new Douban();
                    douban.setName(dataItem.getString("title"));
                    JSONObject rating = dataItem.getJSONObject("rating");
                    douban.setScore(rating.getString("value"));
                    douban.setUrl(Constants.DouBan_ARTICLE_URL+dataItem.getString("36653963"));
                    doubanList.add(douban);
                }
            }else {
                logger.info("访问豆瓣api成功但是响应为空!data:{}",jsonResponse);
            }
        }catch (Exception e){
            logger.info("获取豆瓣数据失败: {}", e.getMessage());
        }
        return doubanList;
    }

}
