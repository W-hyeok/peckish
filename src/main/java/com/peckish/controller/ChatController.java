package com.peckish.controller;

import com.peckish.domain.ChatMessage;
import com.peckish.dto.ChatMessageDTO;
import com.peckish.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageDTO chatMessageDTO) {
        ChatMessage savedMessage = chatService.saveMessage(
                chatMessageDTO.getChatRoomId(),
                chatMessageDTO.getSender(),
                chatMessageDTO.getContent()
        );
        ChatMessageDTO responseDTO = new ChatMessageDTO();
        responseDTO.setChatRoomId(savedMessage.getChatRoom().getId());
        responseDTO.setSender(savedMessage.getSender());
        responseDTO.setContent(savedMessage.getContent());
        responseDTO.setTimestamp(savedMessage.getTimestamp());
        responseDTO.setRead(savedMessage.isRead());

        messagingTemplate.convertAndSend("/topic/chat/" + chatMessageDTO.getChatRoomId(), responseDTO);
    }
}
