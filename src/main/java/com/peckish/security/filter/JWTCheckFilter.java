package com.peckish.security.filter;

import com.google.gson.Gson;
import com.peckish.dto.MemberDTO;
import com.peckish.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@Slf4j
public class JWTCheckFilter extends OncePerRequestFilter {
    // 필터 제외할 요소 지정하는 메서드
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // preflight 체크 제외
        if(request.getMethod().equals("OPTIONS")){
            return true;
        }

        String requestURI = request.getRequestURI();
        log.info(" !!! JWTCheckFilter - shouldNotFilter - requestURI !!! : {} ", requestURI);

        // /api/member/... 로 시작하는 경로는 체크 제외
        if(requestURI.startsWith("/api/member")){
            return true;
        }


        if(requestURI.startsWith("/api/shop")){
            return true;
        }

        if(requestURI.startsWith("/api/")){
            return true;
        }

        // 이미지 조회 경로 체크 제외
        if(requestURI.startsWith("/api/products/view/")){
            return true;
        }

        return false;
    }

    // 필터링 메서드
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info(" !!! JWT Check Filter - doFilterInternal !!! ");
        String authHeaderValue = request.getHeader("Authorization");
        // Bearer AccessToken 값... (예: Bearer eyJ0eXAiOiJ...)
        String accessToken = authHeaderValue.substring(7); // [7] 부터. ("Bearer " 가 [6]까지니까...)
        try {
            Map<String, Object> claims = JWTUtil.validateToken(accessToken); // 예외 발생 가능...
            log.info(" !!! JWT Check Filter - doFilterInternal - claims : {}!!! ", claims); // {password=..., social=... , ...}
            String email = (String) claims.get("email");
            String password = (String) claims.get("password");
            String nickname = (String) claims.get("nickname");
            String phone = (String) claims.get("phone");
            String businessNumber = (String) claims.get("businessNumber");
            String profileFilename = (String) claims.get("profileFilename");
            String certiFilename = (String) claims.get("certiFilename");
            Boolean social = (Boolean) claims.get("social");
            Boolean owned = (Boolean) claims.get("owned");
            int memberStat = (int) claims.get("memberStat");
            List<String> roleNames = (List<String>) claims.get("roleNames");

            // AccessToken에 저장된 사용자 정보를 꺼내어 MemberDTO(UserDetails 타입)에 정보 담아 객체 생성
            MemberDTO memberDTO = new MemberDTO(email, password, nickname, phone, businessNumber, profileFilename, certiFilename, owned, social, memberStat, roleNames);
            log.info(" JWT Check Filter - doFilterInternal - MemberDTO: {}", memberDTO);

            // Security 전용 인증 토큰
            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(memberDTO, password, memberDTO.getAuthorities());

            // Security Context에 토큰 추가 (Security로 로그인한 효과 처리)
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response); // '다음 필터로 진행'
        }catch (Exception e){
            // 검증 예외 처리: AccessToken 검증-에러 발생 시 JSON 으로 에러 메시지 전송
            log.info(" !!!! JWT Check Error !!!!: {}", e.getMessage());
            Gson gson = new Gson();
            log.info("gson: {}", gson);
            String msg = gson.toJson(Map.of("error", "ERROR_ACCESS_TOKEN"));
            log.error("msg: {}", msg);
            response.setContentType("application/json");
            PrintWriter writer = response.getWriter();
            writer.println(msg);
            writer.close();
        }
    }
}
