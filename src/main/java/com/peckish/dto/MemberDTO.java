package com.peckish.dto;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MemberDTO extends User {

    private String email;
    private String password;
    private String nickname;
    private String phone;
    private String profileFilename;
    private String certiFilename;
    private boolean social;
    private int memberStat; // 회원 활동 여부
    //    private LocalDateTime regDate;
//    private LocalDateTime updateDate;
    private List<String> roleNames = new ArrayList<>(); // 롤 이름만 저장

    // 생성자
    public MemberDTO(String email, String password, String nickname, String phone, String profileFilename, String certiFilename, boolean social, int memberStat, List<String> roleNames) {
        super(email, password, roleNames.stream()
                .map(str -> new SimpleGrantedAuthority("ROLE_" + str))
                .collect(Collectors.toList()));
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.phone = phone;
        this.profileFilename = profileFilename;
        this.certiFilename = certiFilename;
        this.social = social;
        this.memberStat = memberStat;
        this.roleNames = roleNames;
    }

    // JWT 를 위한 메서드 : 현재 사용자 정보 Map 타입으로 리턴
    public Map<String, Object> getClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("password", password); // 비번은 원래 빼야함 -> 뷰에 전달X -> 예제라 확인차 추가
        claims.put("nickname", nickname);
        claims.put("phone", phone);
        claims.put("profileFilename", profileFilename);
        claims.put("certiFilename", certiFilename);
        claims.put("social", social);
        claims.put("memberStat", memberStat);
        claims.put("roleNames", roleNames);

        return claims;
    }

}