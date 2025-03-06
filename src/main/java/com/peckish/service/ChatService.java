package com.peckish.service;

import com.peckish.domain.ChatMessage;
import com.peckish.domain.ChatRoom;
import com.peckish.domain.Role;
import com.peckish.domain.Role;
import com.peckish.repository.ChatMessageRepository;
import com.peckish.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    // 고객의 아이디로 채팅방을 조회하거나 없으면 생성
    public ChatRoom createOrGetChatRoom(String customerId) {
        ChatRoom chatRoom = chatRoomRepository.findByCustomerId(customerId);
        if (chatRoom == null) {
            chatRoom = new ChatRoom();
            chatRoom.setCustomerId(customerId);
            chatRoom = chatRoomRepository.save(chatRoom);
        }
        return chatRoom;
    }

    // 채팅 메시지 저장 (기본 read 상태는 false)
    public ChatMessage saveMessage(Long roomId, Role sender, String content) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방이 존재하지 않습니다."));
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatRoom(chatRoom);
        chatMessage.setSender(sender);
        chatMessage.setContent(content);
        chatMessage.setTimestamp(LocalDateTime.now());
        return chatMessageRepository.save(chatMessage);
    }

    // 특정 채팅방의 모든 메시지 조회
    public List<ChatMessage> getMessages(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방이 존재하지 않습니다."));
        return chatMessageRepository.findByChatRoom(chatRoom);
    }

    // 사장님(관리자)에서 모든 채팅방(고객별)을 조회
    public List<ChatRoom> getChatRoomList() {
        return chatRoomRepository.findAll();
    }

    // 특정 채팅방에서 안읽은 메시지 수 조회
    // 예: 관리자가 읽을 때, 고객이 보낸 메시지 중 읽지 않은 수
    public long getUnreadCount(Long roomId, Role sender) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방이 존재하지 않습니다."));
        return chatMessageRepository.countByChatRoomAndSenderAndReadFalse(chatRoom, sender);
    }

    // 특정 채팅방의 메시지를 읽음 처리 (읽은 사용자에 따른 처리)
    // 예: 관리자가 채팅방에 들어왔을 때 고객이 보낸 안읽은 메시지를 읽음 처리
    public void markMessagesAsRead(Long roomId, Role reader) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방이 존재하지 않습니다."));
        List<ChatMessage> messages = chatMessageRepository.findByChatRoom(chatRoom);
        for (ChatMessage msg : messages) {
            if (msg.getSender() != reader && !msg.isRead()) {
                msg.setRead(true);
            }
        }
    }
}
