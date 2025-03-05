package com.peckish.security.handler;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Slf4j
public class APILoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, // 요청 정보 객체
                                        HttpServletResponse response, // 응답 정보 객체
                                        AuthenticationException exception) // exception: 인증 관련 예외 객체를 담을 매개변수
            throws IOException, ServletException {
        log.info(" ---- Authentication failure!! ---- {}", exception.toString());
                // 실패 메시지를 JSON 형태로 응답
                Gson gson = new Gson(); // Gson 객체 생성 (Java 객체를 JSON 문자열로 변환)

                // 응답할 JSON 데이터 준비: Key: "error" / Value: "ERROR_LOGIN" ...의 Map 생성 후 JSON 문자열로 리턴
                String json = gson.toJson(Map.of("error", "ERROR_LOGIN"));

                // 응답 타입을 JSON으로 설정
                response.setContentType("application/json");

                // 클라이언트에게 JSON 형식으로 오류 메시지 전달
                PrintWriter writer = response.getWriter();
                writer.println(json); // 생성된 JSON 문자열 출력
                writer.flush(); // 출력 stream을 플러시하여 데이터가 즉시 전송되도록 함
    }
}
