package com.peckish.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "roleList")
public class Member {
    @Id
    private String email; // id(PK): 이메일
    private String password; // 비밀번호
    private String nickname; // 닉네임
    private String phone;
    private String profileFilename;
    private String certiFilename;
    private boolean social; // 소셜 회원 여부
    private int memberStat; // 회원 활동 여부
    private LocalDateTime regDate;
    private LocalDateTime updateDate;

    @ElementCollection(fetch = FetchType.LAZY) // 테이블 별도 생성
    @Builder.Default
    @Enumerated(EnumType.STRING) // Enum(열거형)을 문자열(설정해둔 이름)로 추가되도록 설정 (미설정 시(기본값) 숫자)
    private List<Role> roleList = new ArrayList<>(); // 권한 목록 (USER, OWNER, ADMIN)

//    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
//    private List<Shop> shops = new ArrayList<>();
//
//    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
//    private List<Visited> visited = new ArrayList<>();
//
//    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
//    private List<Favorite> favorites = new ArrayList<>();

    // 수정 메서드
    // 새 역할 추가
    public void addRole(Role role) {
        roleList.add(role);
    }
    // 특정 역할 제거
    public void removeRole(Role role) {
        roleList.remove(role);
    }
    // pw 변경
    public void changePassword(String password) {
        this.password = password;
    }
    // 닉변
    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }
    // 연락처 변경
    public void changePhone(String phone) {
        this.phone = phone;
    }
    // profileFilename 변경
    public void changeProfileFilename(String profileFilename) {
        this.profileFilename = profileFilename;
    }
    // certiFilename 변경
    public void changeCertiFilename(String certiFilename) {
        this.certiFilename = certiFilename;
    }

    // 소셜 회원(로그인) 여부 변경
    public void changeSocial(boolean social) {
        this.social = social;
    }
    // 회원 탈퇴 처리
    public void changeMemberStat(int memberStat) {
        this.memberStat = memberStat;
    }

    // regDate 시간 변경
    public void changeRegDate(LocalDateTime regDate) {this.regDate = LocalDateTime.now(ZoneId.of("Asia/Seoul"));}

    // update 시간 변경
    public void changeUpdateDate(LocalDateTime updateDate) {this.updateDate = LocalDateTime.now(ZoneId.of("Asia/Seoul"));}


}
