package com.peckish.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {


    public enum MessageType {
        ENTER, TALK
    }
    private Long roomId;
    private MessageType messageType;
    private String sender;
    private String message;
    private String reg_date;
}
