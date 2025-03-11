package com.peckish.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class RoomDTO {
    private Long roomId;
    private String photoPath;

    public RoomDTO(Long roomId, String photoPath) {
        this.roomId = roomId;
        this.photoPath = photoPath;
    }
}