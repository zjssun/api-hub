package com.main.youtube.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class YoutubeWebConfig implements WebMvcConfigurer {

    private final YoutubeProperties properties;

    public YoutubeWebConfig(YoutubeProperties properties) {
        this.properties = properties;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ApiKeyInterceptor(properties)).addPathPatterns("/api/youtube/**");
    }

    static class ApiKeyInterceptor implements HandlerInterceptor {

        private final YoutubeProperties properties;

        ApiKeyInterceptor(YoutubeProperties properties) {
            this.properties = properties;
        }

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                throws IOException {
            String apiKey = properties.getApiKey();
            if (!StringUtils.hasText(apiKey)) {
                return true;
            }
            if (apiKey.equals(request.getHeader("X-Api-Key"))) {
                return true;
            }
            response.setStatus(401);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write("{\"status\":\"error\",\"code\":401,\"info\":\"invalid api key\"}");
            return false;
        }
    }
}
