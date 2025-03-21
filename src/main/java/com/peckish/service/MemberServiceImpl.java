package com.peckish.service;

import com.peckish.domain.Member;
import com.peckish.domain.Role;
import com.peckish.domain.Shop;
import com.peckish.domain.ShopUser;
import com.peckish.dto.*;
import com.peckish.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public MemberDTO getKakaoMember(String accessToken) {
        String email = getEmailFromKakaoAccessToken(accessToken);
        log.info("MemberSerivce - getKakaoMember - email: {}", email);

        Optional<Member> findMember = memberRepository.findById(email);
//        log.info("MemberSerivce - findMember - email: {}", findMember.get());
        // 기존 회원일 경우: DB에서 찾은 Member를 MemberDTO로 변환하여 리턴
        if(findMember.isPresent()) {
            MemberDTO memberDTO = entityToDTO(findMember.get());
            return memberDTO;
        }

        // 신규 회원일 경우: 임시 pw, 임시 닉네임으로 Member(En.) 생성하여 DB에 저장 및 DTO 리턴
        Member socialMember = makeSocialMember(email);
        memberRepository.save(socialMember);
        log.info("MemberSerivce - getKakaoMember - socialMember: {}", socialMember);
        MemberDTO socialMemberDTO = entityToDTO(socialMember);

        return socialMemberDTO;
    }

    @Override
    public String regist(MemberFormDTO memberFormDTO) {

        Boolean isExist = memberRepository.existsByEmail(memberFormDTO.getEmail());
        Boolean isExistPhone = memberRepository.existsByPhone(memberFormDTO.getPhone());
        if (isExist) {
            return "existMember";
        } else if (isExistPhone) {
            return "existPhone";
        }
        if (memberFormDTO.getBusinessNumber() != null) {
            Boolean isExistBusinessNumber = memberRepository.existsByBusinessNumber(memberFormDTO.getBusinessNumber());
            if (isExistBusinessNumber) {
                return "existBusinessNumber";
            }
        }
        // FormDTO를 Entity로 변환 후 저장
        Member member = dtoToEntity(memberFormDTO);
        Member savedMember = memberRepository.save(member); // 작성 정보 DB에 저장
        return savedMember.getEmail(); // 저장된 회원 이메일 반환
    }

    @Override
    public MemberResponseDTO getMemberByEmail(String email) {

        // email로 멤버 1명 조회
        Member member = memberRepository.getMemberWithRoles(email);
        log.info("******* MemberSerivceImpl - getMemberByEmail Member(entity): {}", member.toString());
        // 조회된 Member Entity를 MemberDTO로 변환
        MemberResponseDTO memberResponseDTO = entityToMemberResponseDTO(member);
        log.info("###### MemberSerivceImpl - getMemberByEmail MemberDTO: {}", memberResponseDTO.toString());

        return memberResponseDTO;
    }

    @Override
    public MemberResponseDTO getMemberByPhone(String phone) {
        // 연악처로 멤버 1명 조회
        Member member = memberRepository.getMemberWithRolesByPhone(phone);
        log.info("******* MemberSerivceImpl - getMemberByPhone Member(entity): {}", member.toString());
        // 조회된 Member Entity를 MemberDTO로 변환
        MemberResponseDTO memberResponseDTO = entityToMemberResponseDTO(member);
        log.info("###### MemberSerivceImpl - getMemberByPhone MemberDTO: {}", memberResponseDTO.toString());

        return memberResponseDTO;
    }




    // Member(Entity) → MemberResponseDTO 변환 메서드
    private MemberResponseDTO entityToMemberResponseDTO(Member member) {
        MemberResponseDTO memberResponseDTO = MemberResponseDTO.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .phone(member.getPhone())
                .businessNumber(member.getBusinessNumber())
                .profileFilename(member.getProfileFilename())
                .certiFilename(member.getCertiFilename())
                .social(member.isSocial())
                .memberStat(member.getMemberStat())
                .roleNames(member.getRoleList().stream()
                        .map(role -> role.toString())
                        .collect(Collectors.toList()))
                .build();
        return memberResponseDTO;
    }

    // MemberFormDTO → Member(Entity) 변환 메서드
    private Member dtoToEntity(MemberFormDTO memberFormDTO) {
        Member member = Member.builder()
                .email(memberFormDTO.getEmail())
                .password(passwordEncoder.encode(memberFormDTO.getPassword()))
                .nickname(memberFormDTO.getNickname())
                .phone(memberFormDTO.getPhone())
                .businessNumber(memberFormDTO.getBusinessNumber())
                .profileFilename(memberFormDTO.getProfileFilename())
                .certiFilename(memberFormDTO.getCertiFilename())
                .social(false)
                .owned(false)
                .memberStat(1)
                .regDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .updateDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .build();
        member.addRole(Role.USER);
        log.info("MemberType: {}", memberFormDTO.getMemberType());

        if(memberFormDTO.getMemberType().equals("OWNER")) {
            member.addRole(Role.OWNER); // 사업자 권한
            member.changeMemberStat(2);
        }
        if(memberFormDTO.getMemberType().equals("ADMIN")) {
            member.addRole(Role.OWNER); // 사업자 권한
            member.addRole(Role.ADMIN); // 관리자 권한
        }

        // DTO에 포함된 이미지 파일명 목록을 통해 ProductImage(Entity) 추가
        String profileFilename = memberFormDTO.getProfileFilename();
        String certiFilename = memberFormDTO.getCertiFilename();

        return member;
    }

    // 멤버 정보 수정
    @Override
    public void modifyMember(MemberModifyDTO memberModifyDTO) {
        Member member = memberRepository.findById(memberModifyDTO.getEmail()).orElseThrow();
        member.changePassword(passwordEncoder.encode(memberModifyDTO.getPassword()));
        member.changeSocial(false);
        member.changeNickname(memberModifyDTO.getNickname());
        memberRepository.save(member);
    }

    @Override
    public String modifyMemberInfoService(MemberFormModifyInfoDTO memberFormModifyInfoDTO) {

        // email로 기존 회원 정보 조회
        Member findmember = memberRepository.getMemberWithRoles(memberFormModifyInfoDTO.getEmail());
        log.info("****** 정보 수정 *********: {}", memberFormModifyInfoDTO.getMemberType().toString());

        if (memberFormModifyInfoDTO.getMemberType().equals("OWNER")) {
            findmember.addRole(Role.OWNER); // 사업자 권한
            findmember.changeMemberStat(2);
            // 수정한 내용으로 기존 정보 업데이트
            findmember.changeNickname(memberFormModifyInfoDTO.getNickname());
            findmember.changePhone(memberFormModifyInfoDTO.getPhone());
            findmember.changeBusinessNumber(memberFormModifyInfoDTO.getBusinessNumber());
            findmember.changeUpdateDate(memberFormModifyInfoDTO.getUpdateDate());

            Boolean isExistBusinessNumber = memberRepository.existsByBusinessNumber(memberFormModifyInfoDTO.getBusinessNumber());
                if (isExistBusinessNumber) {
                    return "existBusinessNumber";
                }
        }

        if (memberFormModifyInfoDTO.getMemberType().equals("USER")) {
            findmember.removeRole(Role.OWNER); // 사업자 권한
            findmember.changeMemberStat(1);
            // 수정한 내용으로 기존 정보 업데이트
            findmember.changeNickname(memberFormModifyInfoDTO.getNickname());
            findmember.changePhone(memberFormModifyInfoDTO.getPhone());
            findmember.changeBusinessNumber(null);
            findmember.changeUpdateDate(memberFormModifyInfoDTO.getUpdateDate());
        }

        if (memberFormModifyInfoDTO.getProfileImg() != null) {
            findmember.changeProfileFilename(memberFormModifyInfoDTO.getProfileFilename());
        }
        if (memberFormModifyInfoDTO.getCertiImg() != null) {
            findmember.changeCertiFilename(memberFormModifyInfoDTO.getCertiFilename());
        }


        
        // 수정된 정보 DB에 저장
        memberRepository.save(findmember);
         
        return findmember.getEmail(); // 수정된 회원 이메일 반환
    }

    @Override
    public void modifyMemberKakaoService(MemberFormModifyKakaoDTO memberFormModifyKakaoDTO) {

        // email로 기존 회원 정보 조회
        Member findmember = memberRepository.getMemberWithRoles(memberFormModifyKakaoDTO.getEmail());

        // 수정한 내용으로 기존 정보 업데이트
        findmember.changePassword(passwordEncoder.encode(memberFormModifyKakaoDTO.getPassword()));
        findmember.changeNickname(memberFormModifyKakaoDTO.getNickname());
        findmember.changePhone(memberFormModifyKakaoDTO.getPhone());
        findmember.changeSocial(false);
        findmember.changeUpdateDate(memberFormModifyKakaoDTO.getUpdateDate());
        findmember.changeRegDate(memberFormModifyKakaoDTO.getUpdateDate());

        if (memberFormModifyKakaoDTO.getProfileFilename() != null) {
            findmember.changeProfileFilename(memberFormModifyKakaoDTO.getProfileFilename());
        }

        // 수정된 정보 DB에 저장
        memberRepository.save(findmember);
    }

    @Override
    public String modifyMemberPasswordService(MemberFormModifyPasswordDTO memberFormModifyPasswordDTO) {

        String matchResult="";
        // email로 기존 회원 정보 조회
        Member findmember = memberRepository.getMemberWithRoles(memberFormModifyPasswordDTO.getEmail());

        log.info("비밀번호 확인: findmember: {} ", findmember.getPassword());
        log.info("비밀번호 확인: memberFormModifyPasswordDTO: {} ", memberFormModifyPasswordDTO.getPassword());

        String encodedPassword = passwordEncoder.encode(memberFormModifyPasswordDTO.getPassword());
        log.info("비밀번호 확인: encodedPassword {} ", encodedPassword);

        if (passwordEncoder.matches(memberFormModifyPasswordDTO.getPassword(), findmember.getPassword())) {
            log.info("****** 기존 비밀번호 일치 !!!! ******");
            // 수정한 내용으로 기존 정보 업데이트
            findmember.changePassword(passwordEncoder.encode(memberFormModifyPasswordDTO.getNewPassword()));
            findmember.changeUpdateDate(memberFormModifyPasswordDTO.getUpdateDate());
            // 수정된 정보 DB에 저장
            memberRepository.save(findmember);
            matchResult="일치";

        } else {
            log.info("*******   기존 비밀번호가 일치하지 않습니다.  ******");
            matchResult="불일치";

        }

        return matchResult;
    }

    @Override
    public void remove(String email) {
        // memberStat를 0으로 변경하여 삭제 처리
        memberRepository.updateToDeleteMember(email, 0);
    }

    // 임시 비밀번호로 변경
    @Override
    public void modifyPasswordByTemporaryPassword(String email, String pwdResult) {

        // email로 기존 회원 정보 조회
        Member findmember = memberRepository.getMemberWithRoles(email);

            // 수정한 내용으로 기존 정보 업데이트
            findmember.changePassword(passwordEncoder.encode(pwdResult));
            log.info("암호화된 임시 비밀번호: {}", findmember.getPassword());
            // 수정된 정보 DB에 저장
            memberRepository.save(findmember);
    }

    @Override
    public List<ShopMoonDTO> getShopsByEmail(String email) {
        List<ShopUser> shopUsers = memberRepository.findAllByEmail(email);
        log.info("MemberServiceImpl에서 확인: {}", shopUsers.size());

        /*
        // 리턴해줄 ShopMoonDTO 형태의 리스트를 준비하기
        List<ShopMoonDTO> shopMoonDTOList = new ArrayList<ShopMoonDTO>();
        #1. for문 돌려서, 하나씩 Entity꺼내 ShopMoonDTO로 변환해 리스트에 추가
        for(int i = 0; i < shopUsers.size(); i++){
            ShopUser shopUser = shopUsers.get(i);
            ShopMoonDTO shopMoonDTO = new ShopMoonDTO(shopUser);
            shopMoonDTOList.add(shopMoonDTO);
        }
        //#2. upgrade for문
        for(ShopUser shopUser : shopUsers){
            //ShopUser shopUser = shopUsers.get(i);
            ShopMoonDTO shopMoonDTO = new ShopMoonDTO(shopUser);
            shopMoonDTOList.add(shopMoonDTO);
        }
        */
        // #3. JavaStream 사용
        List<ShopMoonDTO> list = shopUsers.stream()
                .map(shopUser -> new ShopMoonDTO(shopUser))
                .collect(Collectors.toList());

        return list;
    }

    @Override
    public List<MemberResponseDTO> getMembersByBusinessNumber(String businessNumber) {
        // 사업자 등록번호 일치하는 멤버들 조회
        List<Member> members = memberRepository.findAllByBusinessNumber(businessNumber);
        // 조회된 Member Entity를 MemberDTO로 변환
        List<MemberResponseDTO> memberList = members.stream()
                .map(member -> new MemberResponseDTO(member))
                .collect(Collectors.toList());

        return memberList;
    }
    @Override
    public ShopDTO getShopByEmail(String email) {
        // 1. Shop : email로 shop 정보 가져오기
        Shop shop = memberRepository.findOneShopByEmail(email);
        if (shop !=  null) {
            log.info("MemberServiceImpl에서 shop 확인: {}", shop.getShopId());
            ShopDTO shopDTO = new ShopDTO(shop);

            // ShopUser가 있으면
            if(shopDTO.isUserData()) {
                // ShopUser 엔티티를 ShopUserDTO로 변환해 ShopDTO에 추가
                ShopUserDTO shopUserDTO = new ShopUserDTO(shop.getShopUser());
                shopDTO.setShopUserDTO(shopUserDTO);
            }
            if(shopDTO.isOwnerData()) {
                ShopOwnerDTO shopOwnerDTO = new ShopOwnerDTO(shop.getShopOwner());
                shopDTO.setShopOwnerDTO(shopOwnerDTO);
            }
            log.info("MemberServiceImpl에서 shopDTO 확인 : {}", shopDTO.toString());

            return shopDTO;
        }
        return null;

    }

    @Override
    public void openShop(String email) {
        memberRepository.openShopAtRepository(email);
        return;
    }


    @Override
    public void closeShop(String email) {
        memberRepository.closeShopAtRepository(email);
        return;
    }

    // 카카오에 사용자 정보 요청
    private String getEmailFromKakaoAccessToken(String accessToken) {
        // 카카오 사용자 정보 요청 URL
        String kakaoGetUserURL = "https://kapi.kakao.com/v2/user/me";
        if(accessToken == null) {
            throw new RuntimeException("Kakao access token is null");
        }

        // 카카오 서버에 HTTP 요청
        RestTemplate restTemplate = new RestTemplate();

        // 헤더 정보 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

        // 헤더 정보 포함하여 HttpEntity 객체 생성
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 요청 경로 생성해주는 클래스 이용
        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(kakaoGetUserURL).build();

        ResponseEntity<LinkedHashMap> response
                = restTemplate.exchange(uriBuilder.toString(), HttpMethod.GET, entity, LinkedHashMap.class);
        log.info(" ---- MemberService - response: {}", response);
        LinkedHashMap<String, LinkedHashMap> responseBody = response.getBody();
        log.info(" ---- MemberService - responseBody: {}",  response.getBody());

        // 응답 내용 중 카카오 계정 정보 꺼내기
        LinkedHashMap<String, String> kakaoAccount = responseBody.get("kakao_account");
        log.info(" ---- MemberService - kakaoAccount: {}", kakaoAccount);

        return kakaoAccount.get("email"); // 이메일만 꺼내서 리턴
    }

    // 임시 pw 생성 메서드
    private String makeTempPassword() {
        // 문자열 누적 추가(수정)을 위해 String 대신 StringBuffer 사용
        StringBuffer stringBuffer = new StringBuffer();
        // ascii 이용 - 알파벳(65) 랜덤으로 10글자 암호 생성
        for(int i = 0; i < 10; i++) {
            stringBuffer.append((char)((int)(Math.random() * 55) + 65));
        }
        return stringBuffer.toString(); // 문자열로 리턴
    }

    // 이메일 없을 경우 Member Entity 생성하는 메서드
    private Member makeSocialMember(String email) {
        String tempPassword = makeTempPassword();
        log.info("tempPassword: {}", tempPassword);
        String nickname = "Social Member"; // 임시닉
        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(tempPassword))
                .nickname(nickname)
                .memberStat(1)
                .social(true)
                .build();
        member.addRole(Role.USER);
        return member;
    }
}
