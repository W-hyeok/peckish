package com.peckish.controller;

import com.peckish.dto.*;
import com.peckish.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/admin")
@RequiredArgsConstructor // 생성자 자동 주입
public class AdminController {
    private final AdminService adminService;

    // 목록 처리 : .../list?page=1&size=10
    @GetMapping("/list")
    public PageResponseMoonDTO<MemberResponseDTO> list(PageRequestDTO pageRequestDTO) {
        log.info("/admin/list - pageRequestDTO : {}", pageRequestDTO);
        return adminService.list2(pageRequestDTO);
    }

    // 점포 목록 처리 : .../list?page=1&size=10
    @GetMapping("/shoplist")
    public PageResponseDTO<ShopDTO> shoplist(PageRequestDTO pageRequestDTO) {
        log.info("/admin/shoplist - pageRequestDTO : {}", pageRequestDTO);
        return adminService.shoplist(pageRequestDTO);
    }
    // 사업신청자 목록 처리 : .../list?page=1&size=10
    @GetMapping("/memberlist")
    public PageResponseDTO<MemberResponseDTO> memberlist(PageRequestDTO pageRequestDTO) {
        log.info("/admin/memberlist - pageRequestDTO : {}", pageRequestDTO);
        return adminService.memberlist(pageRequestDTO);
    }

    // 멤버 1명 조회
    @GetMapping("/{email}")
    public MemberResponseDTO getMember(@PathVariable("email") String email) {

        log.info("/admin/getMember - pageRequestDTO : {}", email);
        return adminService.getMemberByEmail(email);
    }

    // 멤버 -> 사업자로 승인처리
    @PutMapping("/modifyInfo/{email}")
    public String modifyMemberInfo(@PathVariable(name="email") String email) {
        log.info("modifyInfo - 사업자 승인 또는 승인 취소 처리 email : {}", email);
        //memberFormModifyInfoDTO.setEmail(email);
        //adminService.modifyMember(memberFormModifyInfoDTO);
        adminService.modifyMemberStat(email);
        return email;
    }

    // 사업자 승인 반려 처리(동일 사업자 번호 존재 --> memberStat = 4로 처리 -> 사업자 로그인 시도시 알림 처리)
    @PutMapping("/modifyInfoReturn/{email}")
    public String modifyMemberInfoReturn(@PathVariable(name="email") String email) {
        log.info("modifyInfo - 사업자 반려 처리 email : {}", email);
        //memberFormModifyInfoDTO.setEmail(email);
        //adminService.modifyMember(memberFormModifyInfoDTO);
        adminService.modifyMemberStat4(email);
        return email;
    }


    @DeleteMapping("/{email}")
    public Map<String, String> remove(@PathVariable("email") String email) {
        log.info("------------delete member------------");
        log.info("email: " + email);
        adminService.remove(email); // delFlag = true 로 변경
        return Map.of("삭제 결과: ", "성공");
    }

}
