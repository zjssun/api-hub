package com.main.constant;

public class Constants {
    public static final Long TWO_MONTH_TIME = 5184000000L;

    public static final String THE_PAPER_URL = "https://cache.thepaper.cn/contentapi/wwwIndex/rightSidebar";
    public static final String THE_PAPER_ARTICLE_URL = "https://www.thepaper.cn/newsDetail_forward_";
    public static final String JUEJIN_URL = "https://api.juejin.cn/content_api/v1/content/article_rank?category_id=1&type=hot";
    public static final String JUEJIN_ARTICLE_URL = "https://juejin.cn/post/";

    //redis
    public static final String THE_PAPER_REDIS_KEY = "thePaper";
    public static final Integer REDIS_TIME_THRITY_MIN = 1800000;
    public static final String JUEJIN_REDIS_KEY = "juejin";
}
