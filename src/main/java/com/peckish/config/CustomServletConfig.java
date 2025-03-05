package com.peckish.config;

import com.peckish.controller.formatter.LocalDateFormatter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CustomServletConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new LocalDateFormatter());
    }

    /* // CORS 설정 → Spring Security 설정으로 변경
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // CORS 적용할 URL 패턴
                .allowedOrigins("*") // 리소스 공유를 허용할 origin 설정 (*: 모든 출처에서의 요청 허용). "http://localhost:5173"
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD") // 허용할 HTTP 메서드
                .maxAge(300) // 본 요청 전 예비 요청(preflight request)을 caching 해두는 시간 (초)
                .allowedHeaders("Authorization", "Cache-control", "Content-Type"); // 요청 헤더에 대한 허용 항목 설정
    } */
}
