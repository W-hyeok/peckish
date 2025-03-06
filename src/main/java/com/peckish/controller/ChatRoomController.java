package com.peckish.controller;

import com.peckish.domain.ChatRoom;
import com.peckish.domain.Role;
import com.peckish.dto.ChatMessageDTO;
import com.peckish.dto.ChatRoomDTO;
import com.peckish.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/chat")
public class ChatRoomController {

    private final ChatService chatService;

    // 채팅방 생성 또는 조회 (이미 존재하면 기존 채팅방 반환)
    @PostMapping("/room")
    public ChatRoomDTO createOrGetChatRoom(@RequestBody ChatRoomDTO chatRoomDTO) {
        // 클라이언트로부터 customerId가 담긴 DTO를 받아 처리
        ChatRoom room = chatService.createOrGetChatRoom(chatRoomDTO.getCustomerId());
        ChatRoomDTO responseDTO = new ChatRoomDTO();
        responseDTO.setId(room.getId());
        responseDTO.setCustomerId(room.getCustomerId());
        responseDTO.setCreatedAt(room.getCreatedAt());
        log.info("채팅방 생성");
        return responseDTO;
    }

    // 전체 채팅방 리스트 조회 (고객별)
    @GetMapping("/rooms")
    public List<ChatRoomDTO> getChatRooms() {
        List<ChatRoom> rooms = chatService.getChatRoomList();
        return rooms.stream().map(room -> {
            ChatRoomDTO chatRoomDTO = new ChatRoomDTO();
            chatRoomDTO.setId(room.getId());
            chatRoomDTO.setCustomerId(room.getCustomerId());
            chatRoomDTO.setCreatedAt(room.getCreatedAt());
            return chatRoomDTO;
        }).collect(Collectors.toList());
    }

    // 특정 채팅방의 메시지 내역 조회
    @GetMapping("/rooms/{roomId}/messages")
    public List<ChatMessageDTO> getChatMessages(@PathVariable Long roomId) {
        return chatService.getMessages(roomId).stream().map(message -> {
            ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
            chatMessageDTO.setChatRoomId(message.getChatRoom().getId());
            chatMessageDTO.setSender(message.getSender());
            chatMessageDTO.setContent(message.getContent());
            chatMessageDTO.setTimestamp(message.getTimestamp());
            chatMessageDTO.setRead(message.isRead());
            return chatMessageDTO;
        }).collect(Collectors.toList());
    }

    // 특정 채팅방의 안읽은 메시지 수 조회
    // 예: 관리자가 고객 메시지 중 안읽은 개수를 확인 (sender 파라미터에 CUSTOMER 전달)
    @GetMapping("/rooms/{roomId}/unreadCount")
    public long getUnreadCount(@PathVariable Long roomId, @RequestParam Role sender) {
        return chatService.getUnreadCount(roomId, sender);
    }

    // 특정 채팅방의 메시지 읽음 처리 (읽은 사용자에 따른 처리)
    @PostMapping("/rooms/{roomId}/markAsRead")
    public void markAsRead(@PathVariable Long roomId, @RequestParam Role reader) {
        chatService.markMessagesAsRead(roomId, reader);
    }
}
