package com.main.controller;

import com.main.entity.vo.ResponseVO;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class Introduce extends ABaseController{
    @RequestMapping("/")
    public ResponseVO index(){
        Map<String,Object> map = new HashMap<>();
        map.put("github", "https://github.com/zjssun/api-hub");
        map.put("message", "🎉Welcome to my interface application！");
        map.put("endpoints", new String[]{
                "/hotlist/paper - Latest news highlights from The Paper",
                "/hotlist/juejin - Hot tech posts from Juejin",
                "/hotist/doubanea - Trending Western shows on Douban",
                "/hotlist/36kr - Hot business/tech articles from 36Kr"
        });
        return getSuccessResponseVO(map);
    }
}
