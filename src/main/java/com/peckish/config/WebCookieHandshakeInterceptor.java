package com.peckish.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class WebCookieHandshakeInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 요청에서 쿠키 추출

        if (request instanceof org.springframework.http.server.ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
            Cookie[] cookies = httpServletRequest.getCookies();

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("member".equals(cookie.getName())) { // 'member' 쿠키 확인
                        String decodedValue = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                        String email = extractEmail(decodedValue);
                        if (email != null) {
                            attributes.put("email", email); // WebSocket 세션에 저장
                        }
                        break;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }

    private String extractEmail(String cookieValue) {
        // 쿠키 값이 JSON 형식으로 저장된 경우 email 값을 추출
        if (cookieValue.contains("\"email\":")) {
            int start = cookieValue.indexOf("\"email\":\"") + 9;
            int end = cookieValue.indexOf("\"", start);
            if (start > 8 && end > start) {
                return cookieValue.substring(start, end);
            }
        }
        return null;
    }
}
