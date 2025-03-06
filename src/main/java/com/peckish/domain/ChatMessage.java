package com.peckish.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "chat_message")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 해당 메시지가 속한 채팅방 (다대일 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    // 메시지 발신자: USER 또는 OWNER
    @Enumerated(EnumType.STRING)
    private Role sender;

    private String content; // 메시지 내용
    private LocalDateTime timestamp = LocalDateTime.now(); // 메시지 전송 시각

    @Column(name = "is_read")
    private boolean read = false; // 읽음 상태 (기본 false)
}
