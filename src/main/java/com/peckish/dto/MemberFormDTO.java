package com.peckish.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Data
@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberFormDTO {
    private String email;
    private String password;
    private String nickname;
    private String phone;
    private MultipartFile profileImg;
    private String profileFilename;
    private MultipartFile certiImg;
    private String certiFilename;
    private String memberType;
    private int memberStat;
}
