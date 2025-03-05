package com.peckish.controller;


import com.peckish.dto.*;
import com.peckish.repository.ShopRepository;
import com.peckish.service.MapService;
import com.peckish.service.MemberService;
import com.peckish.service.ShopService;
import com.peckish.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopController {

    private final FileUtil fileUtil;
    private final ShopService shopService;
    private final MapService mapService;
    private final MemberService memberService;
    private final ShopRepository shopRepository;


    //상점 등록
    @PostMapping("/add")
    public Map<String, Long> shopAdd(ShopDTO shopDTO, ShopDetailDTO shopDetailDTO, MapDTO mapDTO) {

        log.info("상점 등록 - shopDTO : {}", shopDTO.toString());
        log.info("상점 등록 - shopDetailDTO : {}", shopDetailDTO.toString());
        log.info("점포 위치 등록 - MapDTO : {} ", mapDTO.toString());

        // 데이터 넘겨서 서비스 호출
        // 파일정보 먼저 처리(파일 저장해서 저장된 파일명 돌려받기, 돌려받은 파일명 dto넣기)
        MultipartFile file = shopDetailDTO.getShopfile(); // dto에서 파일데이터만 꺼내기
        String shopFilename = fileUtil.saveFile(file); // 파일저장->저장된파일명 리턴받음
        //List<String> uploadFileNames = fileUtil.saveFiles(file); 파일이 여러개일 경우
        log.info("파일 업로드 - shopFilename : {} ", shopFilename);

        shopDetailDTO.setShopFilename(shopFilename); //저장된 파일명 DTO String타입에 저장

        //service 호출 - DB 저장
        Long shopId = shopService.add(shopDTO, shopDetailDTO, mapDTO);

        return Map.of("RESULT", shopId);
    }

    // 제보 정보 추가 등록
    @PostMapping("/add/{shopId}/USER")
    public Map<String, Long> ExtraShopUser(@PathVariable("shopId")Long shopId, ShopDTO shopDTO, ShopDetailDTO shopDetailDTO, MapDTO mapDTO){
        log.info("정보 추가 - shopId : {}", shopId);
        log.info("shopdetailDTO *******: {}", shopDetailDTO.toString());
       
        MultipartFile file = shopDetailDTO.getShopfile();
        String shopFilename = fileUtil.saveFile(file);

        log.info("파일 업로드 - shopFilename: {}", shopFilename);

        shopDetailDTO.setShopFilename(shopFilename);

        Long savedshopID = shopService.addShopUser(shopId, shopDTO, shopDetailDTO, mapDTO);

        return Map.of("RESULT", savedshopID);
    }

    // 인증 정보 추가 등록
    @PostMapping("/add/{shopId}/OWNER")
    public Map<String, Long> ExtraShopOwner(@PathVariable("shopId")Long shopId, ShopDTO shopDTO, ShopDetailDTO shopDetailDTO, MapDTO mapDTO){
        log.info("정보 추가 할 shopId : {}", shopId);
        log.info("shopdetailDTO ***** : {}", shopDetailDTO.toString());
        
        MultipartFile file = shopDetailDTO.getShopfile();
        String shopFilename = fileUtil.saveFile(file);
        
        log.info("파일 업로드 - shopFilename: {}", shopFilename);
        
        shopDetailDTO.setShopFilename(shopFilename);
        Long savedshopID = shopService.addShopOwner(shopId, shopDTO, shopDetailDTO, mapDTO);
        //shopRepository.save()
        return Map.of("RESULT", savedshopID);
    }


/*
    // 제보,인증 추가 상점등록
    @PostMapping("/add/{shopId}")
    public Map<String, Long> shopAdditional(@PathVariable Long shopId, ShopDTO shopDTO, ShopDetailDTO shopDetailDTO, MapDTO mapDTO){

        log.info("상점 추가 등록 - shopId : {}", shopId);
        shopService.add(shopDTO,shopDetailDTO,mapDTO);

        return Map.of("RESULT", shopId);
    }
*/



    // 상점 메뉴 등록시 기존 목록 요청
    @GetMapping("/addMenu/{shopId}/{infoType}")
    public Map<String, List> menuList(@PathVariable("infoType") String infoType, @PathVariable("shopId") Long shopId) {
        log.info("menuAdd getMenuList - shopId : {}", shopId);
        log.info("menuAdd getMenuList - infoType : {}", infoType);
        List menuList = null;

        if (infoType.equals("USER")) {
            menuList = shopService.getShopUserMenu(shopId);
        } else if (infoType.equals("OWNER")) {
            menuList = shopService.getShopOwnerMenu(shopId);
        }
        return Map.of("RESULT", menuList);
    }

    //상점 메뉴 등록
    @PostMapping("/addMenu/{shopId}/{infoType}")
    public Map<String, Long> menuAdd(MenuFormDTO menuFormDTO, @PathVariable("infoType") String infoType, @PathVariable("shopId") Long shopId) {

        //String infoType = menuFormDTO.getInfoType();
        //Long shopId = shopDTO.getShopId();

        // shop_user_id or shop_owner_id, infoType
        // menu_name, price, 사진 파일 정보, (+ 정할것 : 메뉴 제보자와 샵제보자가 다르면 메뉴테이블에도 등록자 필요)
        MultipartFile file = menuFormDTO.getMenuFile();
        String menuFileName = fileUtil.saveFile(file); //저장된 파일 이름
        log.info("메뉴 업로드 - menuFormDTO : {}", menuFormDTO);
        log.info("파일 업로드 - menuFileName : {}", menuFileName);

        menuFormDTO.setMenuFilename(menuFileName);
        //menuFormDTO에 화면 경로에서 전달받은 infoType, shopId 넣어주기
        menuFormDTO.setInfoType(infoType);
        menuFormDTO.setShopId(shopId);

        Long menuId = shopService.menuAdd(menuFormDTO);

        return Map.of("RESULT", menuId);
    }


    //상점,메뉴 1개 조회
    @GetMapping("/detail/{shopId}")
    public Map<String, ShopDetailRespDTO> getShop(@PathVariable("shopId") Long shopId) {
        log.info("/shop/detail/shopId - shopId : {} ", shopId);

        // shop 정보가져오기 : Shop, ShopUser, ShopOwner 들어있음 (메뉴는 아직)
        ShopDTO shopDTO = shopService.getShop(shopId);
        List<MenuRespDTO> shopUserMenu = null;
        List<MenuRespDTO> shopOwnerMenu = null;

        // shopUser가 있으면 메뉴 가져와봐
        if (shopDTO.isUserData()) {
            shopUserMenu = shopService.getShopUserMenu(shopId);
        }
        // shopOwner가 있으면 메뉴 가져와봐
        if (shopDTO.isOwnerData()) {
            shopOwnerMenu = shopService.getShopOwnerMenu(shopId);
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

    //이미지 조회 : http://localhost:8080/api/shop/view/{확인할 이미지명}
    @GetMapping("/view/{filename}")
    public ResponseEntity<Resource> viewImage(@PathVariable("filename") String filename) {
        return fileUtil.getFile(filename);
    }

    //메뉴 이미지 조회 : http://localhost:8080/api/shop/view

    // 상점 수정 처리
    @PostMapping("/modify/{shopId}/{shopDetailId}/{infoType}")
    public Map<String, String> modifyShopInfo(@PathVariable("shopId") Long shopId,
                                              @PathVariable("shopDetailId") Long shopDetailId,
                                              @PathVariable("infoType") String infoType,
                                              ShopDetailDTO shopDetailDTO) {

        // 수정가능한 정보
        // title, location, 이미지바꾸기, 요일 days, open/close time, 카테고리 + 좌표

        // 이미지 변경된것이 있으면, 새이미지는 파일 저장처리하고 저장된 새이미지파일이름을 ShopDetailDTO의 shopFilename 변수에 저장, 기존 이미지 파일은 삭제
        log.info("modify - shopDTO 수정할 shopId : {}", shopId);
        log.info("modify - infoType 수정할 shopDetailId : {}", shopDetailId);
        log.info("modify - infoType 수정할 infoType : {}", infoType);
        log.info("modify - infoType 수정할 shopDetailDTO : {}", shopDetailDTO);


        //ShopDetailDTO oldDTO = shopService.getShop();
        //MultipartFile shopfile

        //DB 수정처리
        // shop 수정 : shopId, infoType
        shopService.modifyShop(shopId, infoType, shopDetailId, shopDetailDTO);
        //mapService.modify(mapDTO);

        return Map.of("RESULT", "success");
    }

    // 메뉴 삭제
    @DeleteMapping("/deleteMenu/{menuId}/{infoType}")
    public Map<String, String> deleteMenu(@PathVariable("menuId") Long menuId, @PathVariable("infoType") String infoType) {
        log.info("deleteMenu - menuId : {}", menuId);
        log.info("deleteMenu - infoType : {}", infoType);

        shopService.deleteMenu(menuId, infoType);

        return Map.of("RESULT", "success");
    }

    // 상점 삭제
    @DeleteMapping("/delete/{shopId}/{infoType}/{shopDetailId}")
    public String deleteShop(@PathVariable("shopId") Long shopId, @PathVariable("infoType") String infoType, @PathVariable("shopDetailId") Long shopDetailId) {
        log.info("shopId : {}", shopId);
        log.info("infoType : {}", infoType);
        log.info("shopDetailId : {}", shopDetailId);

        shopService.modifyShopStat(shopId, shopDetailId,infoType);
        shopService.updateShopExist(shopId); // 사장데이터와 제보데이터 둘다 없으면 shop exist false 처리

        return "Success";
    }

}
