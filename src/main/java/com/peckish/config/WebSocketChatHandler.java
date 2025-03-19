package com.peckish.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peckish.dto.MsgDTO;
import com.peckish.service.ChatService;
import com.peckish.util.ChatUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketChatHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ChatService chatService;

    // roomId별로 세션을 관리 (동시성 고려)
    private final Map<Long, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    // 사용자별로 세션 관리 (email을 키로)
    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    // 연결 시 세션에 사용자 정보를 등록 (예: handshake 과정에서 email 속성이 설정되어 있어야 함)
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String email = (String) session.getAttributes().get("email");
        log.info("[WebSocket 연결] session ID: {}, email: {}", session.getId(), email);

        if (email != null) {
            userSessions.put(email, session);
            log.info("✅ WebSocket 연결 성공 - 이메일: {}", email);
        } else {
            log.warn("❌ WebSocket 연결 실패 - 이메일 정보 없음");
        }
    }

    // 연결 종료 시 세션 제거
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String email = (String) session.getAttributes().get("email");
        if (email != null) {
            userSessions.remove(email);
        }
        Long roomId = (Long) session.getAttributes().get("roomId");
        if (roomId != null) {
            Set<WebSocketSession> sessions = roomSessions.get(roomId);
            if (sessions != null) {
                sessions.remove(session);
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        log.info("받은 메시지: {}", payload);

        // JSON payload를 MsgDTO로 변환
        MsgDTO msgDTO = ChatUtil.Chat.resolvePayload(payload);
        Long roomId = msgDTO.getROOM_ID();
        String senderEmail = msgDTO.getEmail();

        // 현재 세션에 roomId 저장
        session.getAttributes().put("roomId", roomId);

        msgDTO.setType("chat");

        // 메시지를 DB에 저장
        chatService.handleAction(roomId, session, msgDTO);

        // 해당 채팅방에 세션 추가
        roomSessions.computeIfAbsent(roomId, k -> new CopyOnWriteArraySet<>()).add(session);

        // 채팅 메시지를 같은 방의 모든 세션에 전송
        Set<WebSocketSession> sessionsInRoom = roomSessions.get(roomId);
        if (sessionsInRoom != null) {
            for (WebSocketSession wsSession : sessionsInRoom) {
                if (wsSession.isOpen()) {
                    try {
                        String jsonMessage = objectMapper.writeValueAsString(msgDTO);
                        wsSession.sendMessage(new TextMessage(jsonMessage));
                    } catch (Exception e) {
                        log.error("메시지 전송 오류: {}", wsSession.getId(), e);
                    }
                }
            }
        }

        // 실시간으로 읽지 않은 메시지 업데이트 알림 전송
        notifyUnreadMessageUpdate(roomId, senderEmail, msgDTO);
    }

    // 읽지 않은 메시지 개수를 각 참여자에게 push하는 메서드
    private void notifyUnreadMessageUpdate(Long roomId, String senderEmail, MsgDTO msgDTO) {
        List<String> participantEmails = chatService.getParticipantsEmails(roomId, senderEmail);

        for (String recipientEmail : participantEmails) {
            int unreadCount = chatService.getUnreadCount(roomId, recipientEmail);

            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "unreadUpdate");
            notification.put("roomId", roomId);
            notification.put("unreadCount", unreadCount);
            notification.put("latestContent", msgDTO.getCONTENT());

            try {
                String jsonNotification = objectMapper.writeValueAsString(notification);
                WebSocketSession recipientSession = userSessions.get(recipientEmail);
                if (recipientSession != null && recipientSession.isOpen()) {
                    recipientSession.sendMessage(new TextMessage(jsonNotification));
                }
            } catch (Exception e) {
                log.error("알림 전송 오류 (수신자: {})", recipientEmail, e);
            }
        }
    }
}