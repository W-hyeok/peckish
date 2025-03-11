package com.peckish.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
public class PapagoTranslationService {

    private static final String CLIENT_ID = "b54ba3w4qn"; // 네이버 클라이언트 ID 4wfrkqolon
    private static final String CLIENT_SECRET = "uKg62zpM6sxCJX7WysWAO74zZ0VDuHZupBYOBf0k"; // 네이버 클라이언트 시크릿 J2ZFvmjujYz3MEsEW4yPrWFH3sFsVWGb2pmQFBNH

    private final RestTemplate restTemplate = new RestTemplate();

    public String translateText(String text, String targetLanguage) {
        String apiUrl = "https://naveropenapi.apigw.ntruss.com/nmt/v1/translation";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-NCP-APIGW-API-KEY-ID", CLIENT_ID);
        headers.set("X-NCP-APIGW-API-KEY", CLIENT_SECRET);

        // Papago에서 source 자동 감지
        String sourceLanguage = "auto";

        // 로그 출력
        log.info("------sourceLanguage: {}", sourceLanguage);
        log.info("------targetLanguage: {}", targetLanguage);

        // ✅ 같은 언어라면 번역 실행 안 하고 원본 그대로 반환
        if (sourceLanguage.equals(targetLanguage)) {
            log.warn("❌ 번역 불필요: source와 target이 동일합니다. (text: {})", text);
            return text;
        }

        // API 요청 본문 구성
        Map<String, String> body = new HashMap<>();
        body.put("source", sourceLanguage);
        body.put("target", targetLanguage);
        body.put("text", text);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = new RestTemplate().exchange(apiUrl, HttpMethod.POST, request, Map.class);

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("message")) {
                Map<String, Object> message = (Map<String, Object>) responseBody.get("message");
                if (message != null && message.containsKey("result")) {
                    Map<String, Object> result = (Map<String, Object>) message.get("result");
                    if (result != null && result.containsKey("translatedText")) {
                        return (String) result.get("translatedText");
                    }
                }
            }
        } catch (HttpClientErrorException.BadRequest e) {
            log.error("🚨 번역 요청 오류 (400 Bad Request): {}", e.getMessage());
            return text; // ✅ 오류 발생 시 원본 그대로 반환
        } catch (Exception e) {
            log.error("🚨 번역 중 알 수 없는 오류 발생: {}", e.getMessage());
        }

        return "Translation failed";
    }


}