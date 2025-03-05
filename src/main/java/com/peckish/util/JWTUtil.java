package com.peckish.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

// 편의를 위해 객체 생성 없이 바로 사용가능하게끔 처리
@Slf4j
public class JWTUtil {

    // 0. 암호 키
    private static String key = "123456780123456780123456780123456780"; // 이것 또한 암호화 시켜놓는 것이 더 좋음

    // 1. 토큰 생성 메서드: 토큰에 저장할 정보(payload), 토큰 유효시간(분) 받아 토큰 생성
    public static String generateToken(Map<String, Object> valueMap, int min) {

        // JWT 토큰 서명 검증 시 사용할 변수
        SecretKey secretKey = null;
        try { // 비밀 키를 UTF-8(한글 깨짐 방지) 로 암호화하여 SecretKey 객체 생성
            // Keys.hmacShaKeyFor: SHA 알고리즘 기반의 서명 키 생성
            // JWTUtil.key: private static String key 변수로 정의된 문자열 ("123...890" << 이거)
            // getBytes("UTF-8"): key 문자열을 바이트 배열로 변환
            secretKey = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8")); // 한글 깨짐 방지
            log.info("secretKey!: {}", secretKey);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        // JWT 토큰 생성: builder() 사용
        String jwtStr = Jwts.builder()
                .setHeaderParam("typ", "JWT") // JWT 헤더의 typ 파라미터를 "JWT"로 설정
                .setClaims(valueMap) // 토큰에 담을 데이터(claim) 설정 (사용자 정의 정보)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant())) // 토큰 발행 시간 (현재시간으로 설정)
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant())) // 토큰 만료 시간 설정 (현재 + 유효 시간 * 1분)
                .signWith(secretKey) // 비밀 키를 사용하여 JWT 서명
                .compact(); // JWT 토큰(Header, Payload, Signature) → Base64Url로 암호화하여, 단일 문자열로 변환(compact)
        log.info("jwtStr!: {}", jwtStr);
        return jwtStr; // 생성된 토큰 반환
    }

    // 2. 토큰 검증 메서드 (토큰 유효성 검증, 유효한 토큰이면 토큰에서 정보 추출)
    public static Map<String, Object> validateToken(String token) {
        // 토큰에서 추출한 claim을 저장할 변수
        Map<String, Object> claim = null;

        // 비밀 키를 저장할 변수
        SecretKey secretKey = null;

        try {
            // 비밀 키 생성 (JWT 토큰 서명 시 사용된 키와 동일해야 함)
            secretKey = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));
            // JWT 토큰을 parsing, 서명을 검증
            claim = Jwts.parserBuilder()
                    .setSigningKey(secretKey) // 서명 검증에 사용할 비밀 키 설정
                    .build() // 토큰 parsing 및 검증 수행
                    .parseClaimsJws(token) // jwt parsing 및 검증 (실패 시 에러 발생)
                    .getBody(); // 토큰에 저장된 claims 꺼내기
        } catch (MalformedJwtException malformedJwtException) {
            throw new CustomJWTException("Malformed"); // 잘못된 형식의 토큰이 들어왔을 때의 예외 처리
        } catch (ExpiredJwtException expiredJwtException) {
            throw new CustomJWTException("Expired"); // 만료된 토큰
        } catch (InvalidClaimException invalidClaimException) {
            throw new CustomJWTException("Invalid"); // 유효하지 않은 Claim
        } catch (JwtException jwtException) {
            throw new CustomJWTException("JWTError"); // 그 외의 JWT 관련 예외사항
        } catch (Exception e) {
            throw new CustomJWTException("Exception"); // 나머지 예외사항
        }
        return claim; // claim 정보 반환
    }
}
