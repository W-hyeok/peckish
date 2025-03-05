package com.peckish.service;

import com.peckish.domain.Member;
import com.peckish.domain.Shop;
import com.peckish.dto.*;
import com.peckish.repository.AdminRepository;
import com.peckish.repository.MemberRepository;
import com.peckish.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final ShopRepository shopRepository;
    private final MemberRepository memberRepository;

    //전체회원목록
    @Override
    public PageResponseDTO<MemberResponseDTO> list(PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(), Sort.by("email").descending());
        Page<Member> result = adminRepository.findAll(pageable);

        // totalCount
        long totalCount = result.getTotalElements();
        // builder()에 all 이라고 이름 부여 - builder() 대신 all() 호출, generic은 all 앞에 부착 // 통으로 구조 암기!
        PageResponseDTO<MemberResponseDTO> responseDTO = PageResponseDTO.<MemberResponseDTO>all()
                .list(result.getContent().stream()
                        .map(member -> toMemberResponseDTO(member))
                        .collect(Collectors.toList()))
                .pageRequestDTO(pageRequestDTO)
                .totalCount(totalCount).build();
        return responseDTO;
    }
    //전체점포목록
    @Override
    public PageResponseDTO<ShopDTO> shoplist(PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage()-1,pageRequestDTO.getSize(),Sort.by("shopId").descending());
        Page<Shop> result = shopRepository.findAll(pageable);
        //List<Shop> -> list<ShopDTO>
        List<ShopDTO> list = result.getContent().stream().map(shop -> new ShopDTO(shop)).collect(Collectors.toList());
        long totalCount = result.getTotalElements();
        PageResponseDTO<ShopDTO> responseDTO = PageResponseDTO.<ShopDTO>all().list(list).pageRequestDTO(pageRequestDTO).totalCount(totalCount).build();
        return responseDTO;
    }
    //전체 사업자 신청 목록
    @Override
    public PageResponseDTO<MemberResponseDTO> memberlist(PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(), Sort.by("email").descending());
        Page<Member> result = adminRepository.findAll(pageable);

        // totalCount
        long totalCount = result.getTotalElements();
        // builder()에 all 이라고 이름 부여 - builder() 대신 all() 호출, generic은 all 앞에 부착 // 통으로 구조 암기!
        PageResponseDTO<MemberResponseDTO> responseDTO = PageResponseDTO.<MemberResponseDTO>all()
                .list(result.getContent().stream()
                        .map(member -> toMemberResponseDTO(member))
                        .collect(Collectors.toList()))
                .pageRequestDTO(pageRequestDTO)
                .totalCount(totalCount).build();
        return responseDTO;
    }


    // Member Entity -> MemberResponseDTO 로 변환
    private MemberResponseDTO toMemberResponseDTO(Member member) {
        MemberResponseDTO memberResponseDTO = MemberResponseDTO.builder()
                .email(member.getEmail())
                .social(member.isSocial())
                .nickname(member.getNickname())
                .memberStat(member.getMemberStat())
                .phone(member.getPhone())
                .profileFilename(member.getProfileFilename())
                .certiFilename(member.getCertiFilename())
                .regDate(member.getRegDate()
                )
                .build();
        return memberResponseDTO;
    }

    @Override
    public MemberResponseDTO getMemberByEmail(String email) {

        // email로 멤버 1명 조회
        Member member = adminRepository.getMemberWithRoles(email);
        log.info("*********MemberSerivce - getMemberByEmail Member(entity): {}", member.toString());
        // 조회된 Member Entity를 MemberDTO로 변환
        MemberResponseDTO memberResponseDTO = entityToMemberResponseDTO(member);
        log.info("######MemberSerivce - getMemberByEmail MemberDTO: {}", memberResponseDTO.toString());

        return memberResponseDTO;
    }
    // Member(Entity) → MemberResponseDTO 변환 메서드
    private MemberResponseDTO entityToMemberResponseDTO(Member member) {
        MemberResponseDTO memberResponseDTO = MemberResponseDTO.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .phone(member.getPhone())
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

    // 멤버 정보 수정
    @Override
    public void modifyMember(MemberResponseDTO memberModifyDTO) {
        Member member = memberRepository.findById(memberModifyDTO.getEmail()).orElseThrow();
        //member.changeSocial(false);
        //member.changeNickname(memberModifyDTO.getNickname());
        //member.changeMemberStat(memberModifyDTO.getMemberStat());
        member.changeMemberStat(2);
        adminRepository.save(member);
    }

    @Override
    public void modifyMemberStat(String email) {
        Member findMember = memberRepository.findById(email).orElseThrow();
        findMember.changeMemberStat(1);
    }

    @Override
    public void remove(String email) {
        log.info("Admin service delete member..." + email);

        Optional<Member> result = memberRepository.findById(email);

        Member member = result.orElseThrow(() ->
                new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        // memberStat을 0(탈퇴회원)으로 변경
        member.changeMemberStat(0);

        memberRepository.save(member);
    }

}
