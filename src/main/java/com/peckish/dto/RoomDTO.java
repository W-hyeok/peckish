package com.peckish.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class RoomDTO {
    private Long roomId;
    private String photoPath;
    private Long unreadCount;  // 추가: 각 대화방의 안 읽은 메시지 수
    private String nickname;

    public RoomDTO(Long roomId, String photoPath, Long unreadCount, String nickname) {
        this.roomId = roomId;
        this.photoPath = photoPath;
        this.unreadCount = unreadCount;
        this.nickname = nickname;
    }
}