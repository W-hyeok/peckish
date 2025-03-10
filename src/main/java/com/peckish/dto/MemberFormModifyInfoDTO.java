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
public class MemberFormModifyInfoDTO {
    private String email;
    private String password;
    private String nickname;
    private String phone;
    private String businessNumber;
    private MultipartFile certiImg;
    private String certiFilename;
    private String memberType;
    private LocalDateTime updateDate;
}
