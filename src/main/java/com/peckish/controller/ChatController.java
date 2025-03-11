package com.peckish.controller;

import com.peckish.domain.Msg;
import com.peckish.domain.Room;
import com.peckish.repository.ChatRepository;
import com.peckish.service.ChatService;
import com.peckish.service.PapagoTranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController("/chat/room")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://192.168.100.154:5173"})  // 여러 출처 설정
public class ChatController {
    private final ChatRepository chatRepository;
    private final ChatService chatService;
    public List<Room> findAll() {
        return chatRepository.findAll();
    }

    public Room findRoomById(Long roomId) {
        return chatRepository.findById(roomId).orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));
    }



}