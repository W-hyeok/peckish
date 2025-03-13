package com.peckish.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ROOM_ID;

    private String ROOM_NAME;
    @Enumerated(EnumType.STRING)
    private RoomType TYPE;
    private Long ROOM_LIMIT;
    private LocalDateTime REG_DATE;
}
