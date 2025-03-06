package com.peckish.repository;

import com.peckish.domain.ChatMessage;
import com.peckish.domain.ChatRoom;
import com.peckish.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoom(ChatRoom chatRoom);

    // 특정 채팅방에서 지정 발신자(unread 대상)의 안읽은 메시지 수 조회
    long countByChatRoomAndSenderAndReadFalse(ChatRoom chatRoom, Role sender);
}
