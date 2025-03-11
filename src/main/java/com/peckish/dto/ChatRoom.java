package com.peckish.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Getter
@Setter
public class ChatRoom {

    private final Long roomId;
    private final String name;
    private final Map<Long, Set<WebSocketSession>> roomSessions = new HashMap<>();

    @Builder
    public ChatRoom(Long roomId, String name) {
        this.roomId = roomId;
        this.name = name;
    }

    public void sendMessage(Long roomId, WebSocketSession session, TextMessage message) {
        log.info("roomID : " + roomId + ", session : " + session);
        // 해당 roomId에 속한 세션 목록을 가져옴
        Set<WebSocketSession> sessionsInRoom = roomSessions.get(roomId);

        if (sessionsInRoom != null) {
            // 해당 채팅방에 연결된 모든 세션에 메시지를 전송
            for (WebSocketSession webSocketSession : sessionsInRoom) {
                if (webSocketSession.isOpen() && !webSocketSession.equals(session)) {
                    sendMessageToSession(webSocketSession, message);  // 각 세션에 메시지 전송
                }
            }
        }
    }

    // 세션에 메시지를 전송하는 메소드
    private void sendMessageToSession(WebSocketSession session, TextMessage message) {
        try {
            session.sendMessage(message);
            log.info("메시지를 세션에 전송했습니다. sessionId: {}", session.getId());
        } catch (IOException e) {
            log.error("메시지 전송 실패. sessionId: {}", session.getId(), e);
        }
    }

    public void join(Long roomId, WebSocketSession session) {
        roomSessions.computeIfAbsent(roomId, k -> new HashSet<>()).add(session);
    }

    public static ChatRoom of(Long roomId, String name) {
        return ChatRoom.builder()
                .roomId(roomId)
                .name(name)
                .build();
    }
}