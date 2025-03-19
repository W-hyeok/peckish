package com.peckish.service;

import com.peckish.dto.*;

public interface AdminService {
    // 회원 목록 조회
    PageResponseDTO<MemberResponseDTO> list(PageRequestDTO pageRequestDTO);
    // 점포 목록 조회
    PageResponseDTO<ShopDTO> shoplist(PageRequestDTO pageRequestDTO);
    // 사업자 신청 목록 조회
    PageResponseDTO<MemberResponseDTO> memberlist(PageRequestDTO pageRequestDTO);
    // 멤버 1명 조회
    MemberResponseDTO getMemberByEmail(String email);
    // 회원 일반정보 수정
    void modifyMember(MemberResponseDTO memberModifyDTO);

    // 사업자로 승인 처리
    void modifyMemberStat(String email);

    // 사업자 승인 반려 처리(동일 사업자 번호 존재 --> memberStat = 4로 처리 -> 사업자 로그인 시도시 알림 처리)
    void modifyMemberStat4(String email);

    // 삭제
    void remove(String email);


}
