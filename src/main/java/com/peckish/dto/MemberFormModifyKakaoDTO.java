package com.peckish.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberFormModifyKakaoDTO {
    private String email;
    private String password;
    private String nickname;
    private String phone;
    private MultipartFile profileImg;
    private String profileFilename;
    private LocalDateTime regDate;
    private LocalDateTime updateDate;
}
