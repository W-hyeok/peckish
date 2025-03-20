package com.peckish.config;

import com.peckish.security.handler.APILoginFailureHandler;
import com.peckish.security.handler.APILoginSuccessHandler;
import com.peckish.security.handler.CustomAccessDeniedHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@Slf4j
@EnableMethodSecurity // 메서드별 권한 체크 어노테이션
public class CustomSecurityConfig {
    // SecurityFilterChain 설정: HttpSecurity를 사용하여 보안 관련 설정 처리
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("시큐리티 필터 작동 확인 메세지(moon)");
        // CORS 설정: 외부로부터의 AJAX 요청을 처리하기 위한 CORS 설정
        http.cors(httpSecurityCorsConfigurer -> {
            httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource());
        });
        // SpringSecurity에서의 세션 관리: API 서버는 Stateless로 운영되므로 세션을 생성하지 않음
        http.sessionManagement(sessionConfig -> {
            sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        });
        // CSRF 설정: 기본적으로 CSRF 보호 활성화됨
        http.csrf(csrf -> csrf.disable()); // API 서버는 Stateless로 운영되므로 CSRF 보호 비활성화 (CSRF 보호할 필요 없음)

        // 로그인 설정: 로그인 경로 및 성공/실패처리
        http.formLogin(login -> {
            login.loginPage("/api/member/login"); // 로그인 페이지 URL 설정
            login.successHandler(new APILoginSuccessHandler()); // 로그인 성공 시 실행
            login.failureHandler(new APILoginFailureHandler()); // 로그인 실패 시 실행
        });

        // (체크)필터 추가
       // http.addFilterBefore(new JWTCheckFilter(), UsernamePasswordAuthenticationFilter.class);

        // 접근 제한 처리
        http.exceptionHandling(exception -> {
            exception.accessDeniedHandler(new CustomAccessDeniedHandler());
        });

        // 설정 완료된 SecurityFilterChain 객체 반환
        return http.build();
    }

    // (Security에서의) CORS 설정: 서버에서 외부의 요청을 받아들이도록 설정
    // 기본적으로 모든 출처(Origin)에서의 요청을 허용, 특정 HTTP 메서드와 헤더만 허용하더록 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration(); // CORS 설정 객체 생성
        corsConfiguration.setAllowedOriginPatterns(Arrays.asList("https://hungrymoment.store", "*")); // 리소스 공유를 허용할 origin 설정 (*: 모든 출처에서의 요청 허용). "http://localhost:5173"
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")); // 허용할 HTTP 메서드
        corsConfiguration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type")); // 요청 헤더에 대한 허용 항목 설정
        corsConfiguration.setAllowCredentials(true); // 클라이언트가 쿠키를 사용할 수 있도록 허용

        // CORS 설정을 경로에 매핑
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration); // 모든 경로에 CORS 정책 설정
        return source; // CORS 설정 반환
    }
    
    // 비밀번호 암호화: BCryptPasswordEncoder를 사용
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
