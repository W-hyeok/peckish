package com.peckish.controller;

import com.peckish.util.CustomJWTException;
import com.peckish.util.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@RestController
@Slf4j
public class APIRefreshController {
    @RequestMapping("/api/member/refresh")
    public Map<String, Object> refresh(@RequestHeader("Authorization") String authHeader, String refreshToken) {
        log.info(" !!!! APIRefreshController - refresh - authHeader: {}", authHeader);
        log.info(" !!!! APIRefreshController - refresh - refreshToken: {}", refreshToken);
        // 하하하

        // 1. Refresh Token이 없는 경우 - 예외 처리
        if(refreshToken == null) {
            throw new CustomJWTException("NULL_REFRESH_TOKEN");
        }
        // 2. header 값이 없거나 일치하지 않는 경우 - 예외 처리
        if(authHeader == null || authHeader.length() < 7) {
            throw new CustomJWTException("INVALID_TOKEN");
        }

        String accessToken = authHeader.substring(7);

        // 3-1. Access Token 검증 - 만료(-) - 기존 토큰 그대로 리턴
        if(!checkExpiredToken(accessToken)) {
            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        }

        // 3-2. Access Token 만료(+):
        // 3-2-1. Refresh Token 검증 - 만료(+): 로그인 필요 / 만료(-): 기존 토큰 리턴
        Map<String, Object> claims = JWTUtil.validateToken(refreshToken);
        log.info(" ---- APIRefreshController - refresh - claims: {}", claims);

        // 4-1. 새로운 Access Token 생성 (...하는 메서드 호출)
        String newAccessToken = JWTUtil.generateToken(claims, 60);
        log.info(" ---- APIRefreshController - refresh - newAccessToken: {}", newAccessToken);

        // 4-2. 새로운 Refresh Token 생성 (refreshToken 남은 시간 체크 - 재발행)
        String newRefreshToken = checkRemainTime((Integer) claims.get("exp")) ? JWTUtil.generateToken(claims, 60 * 24) : refreshToken;

        return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);
    }

    // 토큰 유효기간 체크 메서드
    private boolean checkRemainTime(Integer exp) {
        Date expDate = new Date((long) exp * 1000);
        long diff = expDate.getTime() - System.currentTimeMillis();
        long leftMin = diff / (1000 * 60);
        return leftMin < 60; // 1시간 미만이면 true 리턴
    }

    // 토큰 만료 체크 메서드
    private boolean checkExpiredToken(String token) {
        try {
            JWTUtil.validateToken(token);
        }catch(CustomJWTException e) {
            if(e.getMessage().equals("Expired")) {
                return true;
            }
        }
        return false;
    }
}
