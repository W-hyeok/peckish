package com.peckish.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MsgDTO {

    public enum MessageType {
        ENTER, TALK, EXIT
    }

    public enum Status {
        READ, UNREAD
    }

    private Long MSG_ID;
    private Long ROOM_ID;
    private String email;
    private String CONTENT;
    private Status STATUS;
    private String selectedLanguage;
    private Map<String, String> translatedMessage;

    private String reg_date;

    private MessageType messageType;

}