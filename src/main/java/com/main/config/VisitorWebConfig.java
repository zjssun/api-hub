package com.main.config;

import com.main.service.VisitorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 访客标识拦截器：读取 X-Visitor-Id 请求头，异步记录访问。
 * 全站接口生效，无该头的请求直接跳过（不影响旧接口）。
 */
@Configuration
public class VisitorWebConfig implements WebMvcConfigurer {

    private final VisitorService visitorService;

    public VisitorWebConfig(VisitorService visitorService) {
        this.visitorService = visitorService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new VisitorInterceptor(visitorService)).addPathPatterns("/**");
    }

    static class VisitorInterceptor implements HandlerInterceptor {

        private static final String HEADER = "X-Visitor-Id";

        private final VisitorService visitorService;

        VisitorInterceptor(VisitorService visitorService) {
            this.visitorService = visitorService;
        }

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            String uid = request.getHeader(HEADER);
            if (StringUtils.hasText(uid)) {
                visitorService.recordAsync(uid.trim(), clientIp(request));
            }
            return true;
        }

        /** 优先取反向代理透传的真实 IP */
        private String clientIp(HttpServletRequest request) {
            String forwarded = request.getHeader("X-Forwarded-For");
            if (StringUtils.hasText(forwarded)) {
                return forwarded.split(",")[0].trim();
            }
            String realIp = request.getHeader("X-Real-IP");
            if (StringUtils.hasText(realIp)) {
                return realIp.trim();
            }
            return request.getRemoteAddr();
        }
    }
}
