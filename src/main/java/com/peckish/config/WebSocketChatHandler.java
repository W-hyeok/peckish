package com.peckish.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peckish.dto.MsgDTO;
import com.peckish.service.ChatService;
import com.peckish.util.ChatUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketChatHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper = new ObjectMapper(); // ObjectMapper 인스턴스 추가

    private final ChatService chatService;
    // 번역 기능 제거: PapagoTranslationService 삭제

    // roomId를 키로 세션을 관리
    private final Map<Long, Set<WebSocketSession>> roomSessions = new HashMap<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        log.info("받은 메시지!!!: {}", payload);

        // 받은 메시지를 MsgDTO 변환
        MsgDTO msgDTO = ChatUtil.Chat.resolvePayload(payload);
        Long roomId = msgDTO.getROOM_ID();
        String content = msgDTO.getCONTENT();

        // 세션에 roomID 저장
        session.getAttributes().put("roomId", roomId);

        // 번역 기능 제거: 번역 관련 코드를 모두 삭제
        // 예전 코드 예시:
        // Map<String, String> translatedMessages = new HashMap<>();
        // translatedMessages.put("original", content);
        // ...
        // msgDTO.setTranslatedMessage(translatedMessages);
        // log.info("번역된 메시지: {}", msgDTO.getTranslatedMessage());

        // 메시지를 DB에 저장 (번역 기능 제거됨)
        chatService.handleAction(roomId, session, msgDTO);

        // 해당 채팅방에만 메시지 전송
        roomSessions.computeIfAbsent(roomId, k -> new CopyOnWriteArraySet<>()).add(session);

        Set<WebSocketSession> sessionsInRoom = roomSessions.get(roomId);
        if (sessionsInRoom != null) {
            for (WebSocketSession webSocketSession : sessionsInRoom) {
                if (webSocketSession.isOpen()) {
                    try {
                        String jsonMessage = objectMapper.writeValueAsString(msgDTO);
                        log.info("jsonMessage *** : {}", jsonMessage);
                        webSocketSession.sendMessage(new TextMessage(jsonMessage));
                    } catch (Exception e) {
                        log.error("메시지 전송 오류: {}", webSocketSession.getId(), e);
                    }
                }
            }
        }
    }
}
