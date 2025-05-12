package com.main.constant;

public class Constants {
    public static final Long TWO_MONTH_TIME = 5184000000L;

    public static final String THE_PAPER_URL = "https://cache.thepaper.cn/contentapi/wwwIndex/rightSidebar";
    public static final String THE_PAPER_ARTICLE_URL = "https://www.thepaper.cn/newsDetail_forward_";
    public static final String JUEJIN_URL = "https://api.juejin.cn/content_api/v1/content/article_rank?category_id=1&type=hot";
    public static final String JUEJIN_ARTICLE_URL = "https://juejin.cn/post/";
    public static final String DouBan_URL = "https://m.douban.com/rexxar/api/v2/subject/recent_hot/tv?start=0&limit=20&category=tv&type=tv_american&ck=tuI6";
    public static final String DouBan_ARTICLE_URL = "https://movie.douban.com/subject/";
    
    //redis
    public static final String THE_PAPER_REDIS_KEY = "thePaper";
    public static final Integer REDIS_TIME_THRITY_MIN = 1800000;
    public static final String JUEJIN_REDIS_KEY = "juejin";
    public static final String DOUBAN_REDIS_KEY = "douban";
}
