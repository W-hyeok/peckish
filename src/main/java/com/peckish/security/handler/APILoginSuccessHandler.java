package com.peckish.security.handler;

import com.google.gson.Gson;
import com.peckish.dto.MemberDTO;
import com.peckish.util.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Slf4j
public class APILoginSuccessHandler implements AuthenticationSuccessHandler {
    // Spring Security의 인증 성공 시 호출되는 메서드
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, // 클라이언트의 HTTP 요청 정보를 담는 객체
                                        HttpServletResponse response, // 서버가 보낼 HTTP 응답 정보를 담는 객체
                                        Authentication authentication) // 인증된 사용자(principal)의 인증 정보 객체
            throws IOException, ServletException {
        log.info(" ---- Authentication Success!! ---- {}", authentication); // 인증 정보 출력

        // React에 응답해줄 데이터 생성
        // authentication에서 사용자 정보 가져오기
        MemberDTO memberDTO = (MemberDTO) authentication.getPrincipal(); // 인증된 사용자 객체 반환 (DTO로 형변환)
        // 사용자 정보 Map 타입으로 리턴하는 메서드 호출
        Map<String, Object> claims = memberDTO.getClaims(); // 사용자 정보: email, nickname 등을 담은 Map 객체

        // JWTUtil 이용하여 AccessToken, RefreshToken 생성 → claims에 추가
        String accessToken = JWTUtil.generateToken(claims, 10); // claims 바탕으로 access 토큰 생성. 10분 동안 유효
        String refreshToken = JWTUtil.generateToken(claims, 60 * 24); // 상동. 24시간 동안 유효
        // 생성된 토큰(K/V 형태)을 claims에 추가
        claims.put("accessToken", accessToken);
        claims.put("refreshToken", refreshToken);
        log.info(" ---- Access Success! ---- {}", claims);
        log.info(" --- Access Token!! --- {}", accessToken);
        log.info(" ---- Refresh Token!!! ---- {}", refreshToken);

        // Gson 객체 생성 (JSON 변환을 위한 라이브러리)
        Gson gson = new Gson();

        // claims(Map 객체)를 JSON 문자열로 변환
        String jsonStr = gson.toJson(claims); // (= JSON.stringify)

        // 응답 타입 설정 (클라이언트에게 JSON 형식으로 응답)
        response.setContentType("application/json; charset=utf-8" ); // 응답의 콘텐츠 타입을 JSON으로 설정

        // 응답을 전송하기 위한 PrintWriter 객체 생성
        PrintWriter writer = response.getWriter();

        // 변환된 JSON 문자열을 응답 본문에 작성
        writer.print(jsonStr); // 데이터 응답하기: JSON 문자열을 응답 본문에 출력

        // writer 객체 닫기 (stream 닫기)
        writer.close();
    }
}
