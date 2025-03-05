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


    // 삭제
    void remove(String email);


}
