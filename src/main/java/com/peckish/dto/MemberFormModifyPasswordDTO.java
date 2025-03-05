package com.peckish.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Data
@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberFormModifyPasswordDTO {
    private String email;
    private String password;
    private String newPassword;
    private LocalDateTime updateDate;
}
