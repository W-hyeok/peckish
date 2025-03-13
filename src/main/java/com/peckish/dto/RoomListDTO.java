package com.peckish.dto;

import lombok.Data;

@Data
public class RoomListDTO {
    private Long ROOM_ID;
    private String content;
    // 채팅방에 참여한 사장님을 제외한 사용자의 닉네임
    private String userNickname;
    private String photoPath;    // 상대방 프로필 사진 경로
    private Long unreadCount;    // 해당 채팅방의 안읽은 메시지 개수
}
