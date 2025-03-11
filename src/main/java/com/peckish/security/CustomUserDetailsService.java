package com.peckish.security;

import com.peckish.domain.Member;
import com.peckish.dto.MemberDTO;
import com.peckish.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    // 사용자 인증을 위해 Spring Security에서 호출되는 메서드
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("CustomUserDetailsService - loadUserByUsername: {}", username); // 로그인 시도한 문자열 (email)
        // DB에서, 로그인(한) 아이디(username*)로 회원 정보 조회
        // * 매개변수 username = Security의 'username' = Member(Entity)의 email
        Member member = memberRepository.getMemberWithRoles(username);
        if(member == null) { // 해당 정보 없으면 예외 발생
            throw new UsernameNotFoundException("Email(username) Not Found");
        }
        // Member → MemberDTO로 변환 (MemberDTO는 SpringSecurity가 요구하는 UserDetails 타입임)
        MemberDTO memberDTO =  new MemberDTO(member.getEmail(), member.getPassword(), member.getNickname(), member.getPhone(), member.getBusinessNumber(), member.getProfileFilename(), member.getCertiFilename(), member.isSocial(), member.getMemberStat(), member.getRoleList()
                .stream() // stream<Role>. (배열(<Role>) 요소를 순차적/병렬적으로 처리하기 위해 stream() 사용)
                .map(role -> role.name()) // Stream<String>. Role Enum을 String으로 변환
                .collect(Collectors.toList())); // List로 변환
        log.info("CustomUserDetailsService - loadUserByUsername - memberDTO: {}", memberDTO);

        // Spring Security에 UserDetails 객체 반환 (회원 및 권한 정보 포함)
        return memberDTO;
    }
}
