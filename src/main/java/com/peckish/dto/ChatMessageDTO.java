package com.peckish.dto;

import com.peckish.domain.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageDTO {
    private Long chatRoomId;
    private Role sender;
    private String content;
    private LocalDateTime timestamp;
    private boolean read;
}
