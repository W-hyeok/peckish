package com.peckish.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatRoomDTO {
    private Long id;
    private String customerId;
    private LocalDateTime createdAt;
}
