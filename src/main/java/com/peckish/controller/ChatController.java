package com.peckish.controller;

import com.peckish.domain.Room;
import com.peckish.repository.ChatRepository;
import com.peckish.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/chat/room")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://192.168.100.154:5173"})  // 여러 출처 설정
public class ChatController {

    private final ChatService chatService;
    private final ChatRepository chatRepository;

    public List<Room> findAll() {
        return chatRepository.findAll();
    }

    public Room findRoomById(Long roomId) {
        return chatRepository.findById(roomId).orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));
    }

    @PutMapping("/markAsRead/{roomId}")
    public ResponseEntity<Void> markMessagesAsRead(@PathVariable Long roomId,
                                                   @RequestParam String email) {
        chatService.markMessagesAsRead(roomId, email);
        return ResponseEntity.ok().build();
    }

}