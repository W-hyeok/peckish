package com.peckish.controller;

import com.peckish.dto.*;
import com.peckish.repository.ShopRepository;
import com.peckish.service.MemberService;
import com.peckish.service.ShopService;
import com.peckish.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final ShopService shopService;
    private final FileUtil fileUtil;
    private final ShopRepository shopRepository;

    @PostMapping("/")
    public Map<String, String> regist(MemberFormDTO memberFormDTO) {
        log.info("회원등록 - memberFormDTO: {}", memberFormDTO.toString());

        // 프로필 이미지 파일저장 처리
        MultipartFile profileImg = memberFormDTO.getProfileImg();
        MultipartFile certiImg = memberFormDTO.getCertiImg();
        String profileFilename = fileUtil.saveFile(profileImg);
        String certiFilename = fileUtil.saveFile(certiImg);
        log.info("프로필 업로드 - profileFilename: {}", profileFilename);

        // FormDTO에 저장된 파일 이름들 추가
        memberFormDTO.setProfileFilename(profileFilename);
        memberFormDTO.setCertiFilename(certiFilename);

        // service call - store in DB
        String result = memberService.regist(memberFormDTO);

        return Map.of("RESULT", result);
    }

    // 이미지 조회: http://localhost:8080/api/member/view/{확인할 이미지명}.확장자명
    @GetMapping("/view/{profileFilename}")
    public ResponseEntity<Resource> viewImage(@PathVariable("profileFilename") String profileFilename) {
        return fileUtil.getFile(profileFilename);
    }

    // 멤버 1명 조회
    @GetMapping("/{email}")
    public MemberResponseDTO getMember(@PathVariable("email") String email) {
        return memberService.getMemberByEmail(email);
    }

    // 멤버 1명 조회 (연락처로 조회하기 for 이메일 주소 찾기)
    @GetMapping("/phone/{phone}")
    public MemberResponseDTO getMemberByPhone(@PathVariable("phone") String phone) {
        return memberService.getMemberByPhone(phone);
    }

    // 멤버 일반 정보 수정
    @PutMapping("/modify/{email}")
    public Map<String, String> modifyMemberInfo(@PathVariable(name="email") String email, MemberFormModifyInfoDTO memberFormModifyInfoDTO) {
        log.info("modify - 일반 정보 수정: {}", memberFormModifyInfoDTO.toString());

        memberFormModifyInfoDTO.setEmail(email);
        MemberResponseDTO oldDTO = memberService.getMemberByEmail(email); // DB에 저장된 이전 멤버 정보 조회
        // 있으면 기존거 지우고 없으면 기존 유지
        if (memberFormModifyInfoDTO.getCertiImg() != null) {
            String oldCertiFilename = oldDTO.getCertiFilename(); // 기존 사업자 등록증 파일명
            // 실재 파일 삭제
            fileUtil.deleteOneFile(oldCertiFilename);
            MultipartFile newCertiImg = memberFormModifyInfoDTO.getCertiImg(); // 새로 업로드할 프로필 사진
            String newCertiFilename = fileUtil.saveFile(newCertiImg); // 새로운 파일명
            log.info("memberFormDTO: {}", memberFormModifyInfoDTO.toString());
            memberFormModifyInfoDTO.setCertiFilename(newCertiFilename);;
            log.info("memberFormDTO: {}", memberFormModifyInfoDTO.toString());
        }

        if (memberFormModifyInfoDTO.getMemberType().equals("USER")) {
            String oldCertiFilename = oldDTO.getCertiFilename();
            if (oldCertiFilename != null) {
                // 실재 파일 삭제
                fileUtil.deleteOneFile(oldCertiFilename);
            }
        }

        String result = memberService.modifyMemberInfoService(memberFormModifyInfoDTO);

        return Map.of("RESULT", result);
    }

    // 카카오 멤버 정보 수정
    @PutMapping("/modify/kakao/{email}")
    public String modifyMemberKakao(@PathVariable(name="email") String email, MemberFormModifyKakaoDTO memberFormModifyKakaoDTO) {
        log.info("modify - 카카오 회원 정보 수정: {}", memberFormModifyKakaoDTO.toString());

        memberFormModifyKakaoDTO.setEmail(email);
        MemberResponseDTO oldDTO = memberService.getMemberByEmail(email); // DB에 저장된 이전 멤버 정보 조회
        // 있으면 기존거 지우고 없으면 기존 유지
        if (memberFormModifyKakaoDTO.getProfileImg() != null) {
            String oldProfileFilename = oldDTO.getProfileFilename(); // 기존 프로필 파일명

            // 실재 파일 삭제
            fileUtil.deleteOneFile(oldProfileFilename);
            MultipartFile newProfileImg = memberFormModifyKakaoDTO.getProfileImg(); // 새로 업로드할 프로필 사진
            String newProfileFilename = fileUtil.saveFile(newProfileImg); // 새로운 파일명
            log.info("memberFormModifyKakaoDTO: {}", memberFormModifyKakaoDTO.toString());
            memberFormModifyKakaoDTO.setProfileFilename(newProfileFilename);;
            log.info("memberFormModifyKakaoDTO: {}", memberFormModifyKakaoDTO.toString());
        }

        memberService.modifyMemberKakaoService(memberFormModifyKakaoDTO);

        return email;
    }

    // 멤버 비밀번호 수정
    @PutMapping("/modifyPassword/{email}")
    public String modifyMemberPassword(@PathVariable(name="email") String email, MemberFormModifyPasswordDTO memberFormModifyPasswordDTO) {
        log.info("modify - 비밀번호 수정: {}", memberFormModifyPasswordDTO.toString());

        memberFormModifyPasswordDTO.setEmail(email);

        String matchresult = memberService.modifyMemberPasswordService(memberFormModifyPasswordDTO);

        return matchresult;
    }

    // 비밀번호 찾기
    @PutMapping("/searchPassword/{email}/{phone}")
    public String searchPassword(@PathVariable("email") String email, @PathVariable("phone") String phone) {
        log.info("MemberController 비밀번호 찾기 phone: {}", phone);

        String searchResult = "";
        String pwdResult = "";

        try {
            MemberResponseDTO oldDTO = memberService.getMemberByEmail(email); // DB에 저장된 이전 멤버 정보 조회

            log.info("MemberController 비밀번호 찾기 oldDTO: {}", oldDTO.toString());

            if (oldDTO != null && oldDTO.getPhone().equals(phone)) {
                pwdResult = RandomStringUtils.randomAlphanumeric(8);
                log.info("Random pwd: {}", pwdResult);

                memberService.modifyPasswordByTemporaryPassword(email, pwdResult);
                log.info("Check 임시 비밀번호 쿼리 실행");
                searchResult= pwdResult;
            } else {
                searchResult = "failSearchPassword";
            }
        } catch (Exception e) {
            searchResult= "exceptionError";
        }
        return searchResult;
    }

    // 회원 탈퇴
    @DeleteMapping("/delete/{email}")
    public String remove(@PathVariable("email") String email) {
        memberService.remove(email);
        return "탈퇴성공";
    }

    //상점 List 조회(제보한 가게 리스트)
    @GetMapping("/detail/{email}")
    public List<ShopMoonDTO> getShopsByEmail(@PathVariable("email") String email){

        log.info("localhost:8080/member/detail/email : {} ", email);

        // 내가 제보한 shopList 정보가져오기
        List<ShopMoonDTO> shopDTOs = memberService.getShopsByEmail(email);
        log.info("controller에서 확인 : {}", shopDTOs.toString());

        return shopDTOs;
    }

    //상점 1개 조회
    @GetMapping("/getOneShop/{email}")
    public Map<String, ShopDetailRespDTO> getOneShopByEmail(@PathVariable("email") String email) {
        log.info("Member Controller에서 점포관리를 위해 넘어온 email 확인 : {} ", email);

        ShopDTO shopDTO = memberService.getShopByEmail(email);
        if (shopDTO != null) {
            List<MenuRespDTO> shopUserMenu = null;
            List<MenuRespDTO> shopOwnerMenu = null;

            // shopUser가 있으면 메뉴 가져와봐
            if (shopDTO.isUserData()) {
                shopUserMenu = shopService.getShopUserMenu(shopDTO.getShopId());
            }
            // shopOwner가 있으면 메뉴 가져와봐
            if (shopDTO.isOwnerData()) {
                shopOwnerMenu = shopService.getShopOwnerMenu(shopDTO.getShopId());
            }

            // 화면에 전달해줄 데이터를 RespDTO로 취합
            ShopDetailRespDTO shop = ShopDetailRespDTO.builder()
                    .shopDTO(shopDTO)
                    .shopUserDTO(shopDTO.getShopUserDTO())
                    .shopOwnerDTO(shopDTO.getShopOwnerDTO())
                    .menuUserList(shopUserMenu)
                    .menuOwnerList(shopOwnerMenu)
                    .build();


            log.info("shopResp : {}", shop);
            return Map.of("RESULT", shop);
        }

        return Map.of("RESULT", new ShopDetailRespDTO());

    }

    // 영업개시
    @PutMapping("/putOpenShop/{email}")
    public String putOpenShop(@PathVariable("email") String email) {
        log.info("Member Controller에서 영업개시를 위해 넘어온 email 확인 : {} ", email);
        memberService.openShop(email);

        return "openSuccess";
    }

    // 영업종료
    @PutMapping("/putCloseShop/{email}")
    public String putCloseShop(@PathVariable("email") String email) {
        log.info("Member Controller에서 영업 종료를 위해 넘어온 email 확인 : {} ", email);
        memberService.closeShop(email);

        return "closeSuccess";
    }

}
