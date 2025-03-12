package com.peckish.dto;

import com.peckish.domain.Member;
import com.peckish.domain.ShopUser;
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
    private String businessNumber;
    private String profileFilename;
    private String certiFilename;
    private boolean social;
    private int memberStat; // 회원 활동 여부

    private LocalDateTime regDate;
    private LocalDateTime updateDate;

    @Builder.Default
    private List<String> roleNames = new ArrayList<>(); // 롤 이름만 저장

    // 생성자 : Entity -> DTO로 변환+생성
    public MemberResponseDTO(Member member) {
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.phone = member.getPhone();
        this.businessNumber = member.getBusinessNumber();
        this.profileFilename = member.getProfileFilename();
        this.certiFilename = member.getCertiFilename();
        this.regDate = member.getRegDate();
        this.updateDate = member.getUpdateDate();
        this.memberStat = member.getMemberStat();
    }
}
