package com.peckish.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Data
public class MemberResponseDTO {
    private String email;
    private String password;
    private String nickname;
    private String phone;
    private String profileFilename;
    private String certiFilename;
    private boolean social;
    private int memberStat; // 회원 활동 여부

    private LocalDateTime regDate;
    private LocalDateTime updateDate;

    @Builder.Default
    private List<String> roleNames = new ArrayList<>(); // 롤 이름만 저장
}
