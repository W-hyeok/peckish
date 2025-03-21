package com.peckish.service;

import com.peckish.domain.*;
import com.peckish.dto.*;
import com.peckish.repository.*;
import com.peckish.util.FileUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopServiceImpl implements ShopService {


    private final ShopRepository shopRepository;
    private final ShopOwnerRepository shopOwnerRepository;
    private final ShopUserRepository shopUserRepository;
    private final MenuOwnerRepository menuOwnerRepository;
    private final MenuUserRepository menuUserRepository;
    private final MapRepository mapRepository;
    private final MemberRepository memberRepository;
    private final FileUtil fileUtil;

    // 상점 등록
    @Override
    public Long add(ShopDTO shopDTO, ShopDetailDTO shopDetailDTO, MapDTO mapDTO) {

        // 1. shopDTO => ShopEntity 변경 -> shop테이블 저장  -> shop_id 리턴받기
        Shop shopEntity = shopDTO.toEntity();
        Shop savedShop = shopRepository.save(shopEntity);

        Member member = memberRepository.findById(shopDetailDTO.getEmail()).orElseThrow();
        log.info("멤버 정보? {}", member);
        // Member(email=owner@owner.com, nickname=테스트사장, phone=010-1111-2222 social=false, memberStat=1
        //Menu savedMenu = menuOwnerRepository.save(menuEntity);
        //Menu savedMenu = menuUserRepository.save(menu)

        // 2. 체크해서 shop_id 추가해, 사장이나 제보로 저장 -> shop_xxx_id 리턴받기
        // 사장인지, 제보인지 체크
        if (shopDTO.isCertificate()) {
            // 사장으로 처리
            ShopOwner shopOwnerEntity = shopDetailDTO.toShopOwnerEntity();
            savedShop.changeOwnerData(true); // 사장데이터 있다
            /* todo: member column update - isOwned false → true */
            memberRepository.updateIsOwned(member.getEmail());
            shopOwnerEntity.setMember(member); // 작성자 정보 추가
            shopOwnerEntity.setShop(savedShop); // 저장한 위 Shop엔티티 추가
            member.changeOwned(true); // 소유 여부
            Map mapEntity = mapDTO.toEntity(); // 사장이 작성한 map 정보 DB -> Entity
            //map에 저장할 때 shopId 저장
            mapEntity.setShop(savedShop);
            shopOwnerRepository.save(shopOwnerEntity);
            mapRepository.save(mapEntity);
        } else {
            // 제보로 처리
            ShopUser shopUserEntity = shopDetailDTO.toShopUserEntity();
            savedShop.changeUserData(true); // 제보데이터 있다
            shopUserEntity.setMember(member); // User 작성자 추가
            shopUserEntity.setShop(savedShop);
            shopUserRepository.save(shopUserEntity);
            Map mapEntity = mapDTO.toEntity(); // 제보자가 작성한 map 정보 DB -> Entity
            //map에 저장할 때 shopId 저장
            mapEntity.setShop(savedShop);
            mapRepository.save(mapEntity);
        }
        return savedShop.getShopId(); // shop_id 리턴
    }
    // 제보 정보 추가 등록
    @Override
    public Long addShopUser(Long shopId, ShopDTO shopDTO, ShopDetailDTO shopDetailDTO, MapDTO mapDTO) {
        // 경로에서 받은 shopId
        // 실제 DB에 저장되어있는 shop테이블 shop 정보
        Shop shopEntity = shopRepository.findById(shopId).orElseThrow();
        log.info("addShopUser : {} ", shopId);

        // DTO -> ShopUserEntity
        ShopUser shopUserEntity = shopDetailDTO.toShopUserEntity();
        shopUserEntity.setShop(shopEntity); //shopId

        // shopUserEntity.setShop(shopEntity);

        // isUserData = true
        shopEntity.changeUserData(true);
        // shopUser isExist = true
        shopUserEntity.changeisExist(true);

        shopUserRepository.save(shopUserEntity);

        return shopId;

    }

    // 인증 정보 추가 등록
    @Override
    public Long addShopOwner(Long shopId, ShopDTO shopDTO, ShopDetailDTO shopDetailDTO, MapDTO mapDTO) {
        // 경로에서 받은 Id 저장
        Shop shopEntity = shopRepository.findById(shopId).orElseThrow();
        log.info("addShopOwner : {}", shopId);

        // DTO->Entity
        ShopOwner shopOwnerEntity = shopDetailDTO.toShopOwnerEntity();
        shopOwnerEntity.setShop(shopEntity);
        
        // 인증 정보 true
        shopEntity.changeOwnerData(true);
        // shop isExist true
        shopEntity.changeExist(true);

       shopOwnerRepository.save(shopOwnerEntity);

        return shopId;
    }

  // 메뉴 등록
    @Override
    public Long menuAdd(MenuFormDTO menuFormDTO) {

        log.info("메뉴 등록 - menuUserDTO : {} ", menuFormDTO.toString());
        Long savedId = null; //각 테이블에 저장된 메뉴 pk
        //menuFormDTO.getInfoType() 값에 따른 분기처리
        if (menuFormDTO.getInfoType().equals("USER")) {
            //infoType = "USER" 일 때
            MenuUser menuUserEntity = menuFormDTO.toMenuUserEntity();
            // 추가할 데이터 : ShopUser 객체
            //menuFormDTO.getShopDetailId 호출해서 저장할 테이블 결정
            // .orElseThrow() : 변수로 선언할 때 Optional<> 타입으로 받아서
            ShopUser findShopUser = shopUserRepository.findById(menuFormDTO.getShopDetailId()).orElseThrow();
            menuUserEntity.setShopUser(findShopUser);
            MenuUser save = menuUserRepository.save(menuUserEntity);
            savedId = save.getMenuUserId();

        } else if (menuFormDTO.getInfoType().equals("OWNER")) {
            //infoType = "OWNER" 일 때
            MenuOwner menuOwnerEntity = menuFormDTO.toMenuOwnerEntity();
            //ShopOwnerRepository 사용, ShopOwner 테이블의 id값 저장
            ShopOwner findShopOwner = shopOwnerRepository.findById(menuFormDTO.getShopDetailId()).orElseThrow();
            //ShopOwner테이블의 번호와 menuOwner테이블의 번호 맞추기 / shopId와는 다른 번호
            menuOwnerEntity.setShopOwner(findShopOwner);
            MenuOwner save = menuOwnerRepository.save(menuOwnerEntity);
            savedId = save.getMenuOwnerId();

        }
        return savedId;
    }

    // 상점 상세보기
    @Override
    public ShopDTO getShop(Long shopId) {
        // 1. Shop : shopId 정보 가져오기
        Shop shop = shopRepository.findById(shopId).orElseThrow();
        ShopDTO shopDTO = new ShopDTO(shop); // 위에서 받은 엔티티를 DTO로 변환 이때 생성자 사용함 (빌더패턴 X)
        // ShopUser가 있으면
        if(shopDTO.isUserData()) {
            // ShopUser 엔티티를 ShopUserDTO로 변환해 ShopDTO에 추가
            ShopUserDTO shopUserDTO = new ShopUserDTO(shop.getShopUser());
            shopDTO.setShopUserDTO(shopUserDTO);
            log.info("shopUserDTO??: {}", shopUserDTO);
        }
        if(shopDTO.isOwnerData()) {
            ShopOwnerDTO shopOwnerDTO = new ShopOwnerDTO(shop.getShopOwner());
            shopDTO.setShopOwnerDTO(shopOwnerDTO);
            log.info("shopOwnerDTO??: {}", shopOwnerDTO);

        }
        log.info("shopDTO : {}", shopDTO);
        return shopDTO;



        /*
        // * DB에서 shop 상세정보 조회
        ShopUser shopUser = null;
        ShopOwner shopOwner = null;
        List<MenuUser> menuUser = null;
        List<MenuOwner> menuOwner = null;
        // 2-1. ShopDTO : isUserData, isOwnerData
        // - USER 정보
        if(shopDTO.isUserData()){
            // shopUser + menu들 한방에 조회 -> Entity
            shopUser = shopUserRepository.selectShopUserByShopId(shopId);
            log.info("shopUser : {}", shopUser.getShopUserId());
        // 2-2. Shop - OWNER 정보
        }else if(shopDTO.isOwnerData()){
            shopOwner = shopOwnerRepository.findById(shopId).orElseThrow();
            log.info("shopOwner : {}", shopOwner.getShopOwnerId());
        }

        // * 조회한 정보 변환
        // Entity들 -> DTO로 변경

        ShopUserDTO shopUserDTO = null;
        ShopOwnerDTO shopOwnerDTO = null;
        List<MenuUserDTO> menuUserList = null;
        List<MenuOwnerDTO> menuOwnerList = null;

        if(shopUser != null) {
            // Entity를 DTO로 변환해서 담기
            shopUserDTO = new ShopUserDTO(shopUser);
            log.info("shopUserDTO : {}",shopUserDTO);
            if(!shopUser.getMenuUser().isEmpty()) {
                //메뉴 리스트 사용하기 위해서 리스트 선언
                menuUserList = new ArrayList<>();
                //shopUser : 메뉴와 상점 정보 조회한 , menuUser 정보만 가져오기
                // -> List<MenuUser> : 샵 아이디에 따른 메뉴 엔티티들
                List<MenuUser> menuUserEntities = shopUser.getMenuUser();
                //메뉴 엔티티들을 for문을 사용하여 하나씩 뽑아내기
                for(int i = 0; i < menuUserEntities.size(); i++){
                    MenuUser menuUserEntity = menuUserEntities.get(i);
                    MenuUserDTO menuUserDTO = new MenuUserDTO(menuUserEntity);
                    //DTO menuUserList에 추가하기
                    menuUserList.add(menuUserDTO);
                }
            }

        }
        if(shopOwner != null) {
            shopOwnerDTO = new ShopOwnerDTO(shopOwner);
            log.info("shopOwnerDTO : {}",shopOwnerDTO);
            if(!shopOwner.getMenuOwner().isEmpty()) {
                menuOwnerList = new ArrayList<>();
                List<MenuOwner> menuOwnerEntities = shopOwner.getMenuOwner();
                for(int i=0; i<menuOwnerEntities.size();i++){
                    MenuOwner menuOwnerEntity = menuOwnerEntities.get(i);
                    MenuOwnerDTO menuOwnerDTO = new MenuOwnerDTO(menuOwnerEntity);
                    menuOwnerList.add(menuOwnerDTO);
                }
            }
        }

        // shop 정보
        // shopUser & menu
        // shopOwner & menu
        // 엔티티 5개를 1개의 DTO로
        ShopDetailRespDTO respDTO = ShopDetailRespDTO.builder()
                .shopDTO(shopDTO)
                .shopOwnerDTO(shopOwnerDTO)
                .menuUserList(menuUserList)
                .shopUserDTO(shopUserDTO)
                .menuOwnerList(menuOwnerList)
                .build();

        return respDTO;*/

    }

    // User 메뉴
    @Override
    public List<MenuRespDTO> getShopUserMenu(Long shopId) {
        // ShopUser와 메뉴들 가져오기
        ShopUser shopUser = shopUserRepository.selectShopUserByShopId(shopId);
        // 메뉴가 있으면 아래와 같이 메뉴들 꺼내서 메뉴엔티티를 DTO로 변환해 메뉴DTO 리스트로 리턴
        if(!shopUser.getMenuUser().isEmpty()) {
            // menuUser 엔티티를 Stream 사용하여 반복문 돌리기
            List<MenuRespDTO> menuUserList = shopUser.getMenuUser().stream()
                    //menuEntity를 MenuUserDTO로 바꾸어
                    .map(menuEntity -> new MenuRespDTO(menuEntity))
                    // list 형태로 수집하여 저장
                    .collect(Collectors.toList());
            return menuUserList;

            // 아래와 동일
          /*
            menuUserList = new ArrayList<>();
            //shopUser : 메뉴와 상점 정보 조회한 , menuUser 정보만 가져오기
            // -> List<MenuUser> : 샵 아이디에 따른 메뉴 엔티티들
            List<MenuUser> menuUserEntities = shopUser.getMenuUser();
            //메뉴 엔티티들을 for문을 사용하여 하나씩 뽑아내기
            for(int i = 0; i < menuUserEntities.size(); i++){
                MenuUser menuUserEntity = menuUserEntities.get(i);
                MenuUserDTO menuUserDTO = new MenuUserDTO(menuUserEntity);
                //DTO menuUserList에 추가하기
                menuUserList.add(menuUserDTO);
             */
        }else {
        // 만약 메뉴가 없으면 isEmpty() == true -> null 리턴
            return new ArrayList<MenuRespDTO>();
        }
    }

    // Owner 메뉴
    @Override
    public List<MenuRespDTO> getShopOwnerMenu(Long shopId) {
        // ShopOwner와 메뉴들 가져오기
        ShopOwner shopOwner = shopOwnerRepository.selectShopOwnerByShopUserId(shopId);
        if(!shopOwner.getMenuOwner().isEmpty()) {
            List<MenuRespDTO> menuOwnerList = shopOwner.getMenuOwner().stream()
                    .map(menuEntity -> new MenuRespDTO(menuEntity))
                    .collect(Collectors.toList());
            return menuOwnerList;

        }else{
            // 메뉴가 없는 상황
            // isempty() == true
            return new ArrayList<MenuRespDTO>();
        }
    }
    
    //상점 정보 수정
    @Override
    public void modifyShop(Long shopId, String infoType, Long shopDetailId, ShopDetailDTO shopDetailDTO) {

        if(infoType.equals("USER")){
            ShopUser findShopUser = shopUserRepository.findById(shopDetailId).orElseThrow(); // DB에서 찾아온 정보 담은 Entity

            findShopUser.changeTitle(shopDetailDTO.getTitle());
            findShopUser.changeCategory(shopDetailDTO.getCategory());
            findShopUser.changeDays(shopDetailDTO.getDays());
            findShopUser.changeLocation(shopDetailDTO.getLocation());
            findShopUser.changeOpentime(shopDetailDTO.getOpenTime());
            findShopUser.changeCloseTime(shopDetailDTO.getCloseTime());
            findShopUser.changeUpdateDate(LocalDateTime.now());
            // 새로 등록하는 이미지가 있으면
            if(shopDetailDTO.getShopfile() != null){
                // 기존 이미지가 있으면 (수정하기 전에 저장된 이미지) 이미지파일 삭제 (old 이미지 삭제)
                if(findShopUser.getFilename() != null) {
                    //list 형식으로 저장해야 하기 때문에 asList로 파일을 리스트형식으로 바꿔줌
                    fileUtil.deleteFile(Arrays.asList(findShopUser.getFilename()));
                }
                // 이미지 저장 처리 : 화면에서 받아온 수정 이미지 파일데이터 꺼내서 upload에 저장하고, 저장된 uuid 이름 리턴받아 변수에 저장
                String savedUpdatedFilename = fileUtil.saveFile(shopDetailDTO.getShopfile());
                // 새로 저장된 이미지 파일명 shopDetailDTO.setShopFilename 으로 저장
                shopDetailDTO.setShopFilename(savedUpdatedFilename);
                //shopUser에 shoopFilename명 새로 저장
                findShopUser.changeFileName(shopDetailDTO.getShopFilename()); // DB에 새 이미지 저장
            }


        }else if(infoType.equals("OWNER")){
            ShopOwner findShopOwner = shopOwnerRepository.findById(shopDetailId).orElseThrow();

            findShopOwner.changeTitle(shopDetailDTO.getTitle());
            findShopOwner.changeCategory(shopDetailDTO.getCategory());
            findShopOwner.changeDays(shopDetailDTO.getDays());
            findShopOwner.changeLocation(shopDetailDTO.getLocation());
            findShopOwner.changeOpentime(shopDetailDTO.getOpenTime());
            findShopOwner.changeCloseTime(shopDetailDTO.getCloseTime());
            findShopOwner.changeFileName(shopDetailDTO.getShopFilename());
            findShopOwner.changeUpdateDate(shopDetailDTO.getUpdateDate());
            //수정된 정보 DB에 저장

            // 새로 등록하는 이미지가 있으면
            if(shopDetailDTO.getShopfile() != null){
                // 기존 이미지가 있으면 (수정하기 전에 저장된 이미지) 이미지파일 삭제 (old 이미지 삭제)
                if(findShopOwner.getFilename() != null) {
                    //list 형식으로 저장해야 하기 때문에 asList로 파일을 리스트형식으로 바꿔줌
                    fileUtil.deleteFile(Arrays.asList(findShopOwner.getFilename()));
                }
                // 이미지 저장 처리 : 화면에서 받아온 수정 이미지 파일데이터 꺼내서 upload에 저장하고, 저장된 uuid 이름 리턴받아 변수에 저장
                String savedUpdatedFilename = fileUtil.saveFile(shopDetailDTO.getShopfile());
                // 새로 저장된 이미지 파일명 shopDetailDTO.setShopFilename 으로 저장
                shopDetailDTO.setShopFilename(savedUpdatedFilename);
                //shopUser에 shoopFilename명 새로 저장
                findShopOwner.changeFileName(shopDetailDTO.getShopFilename()); // DB에 새 이미지 저장
            }
        }
    }

    // 메뉴 삭제
    @Override
    public void deleteMenu(Long menuId, String infoType) {
        if(infoType.equals("USER")) {
            menuUserRepository.deleteById(menuId);
        }else if(infoType.equals("OWNER")) {
            menuOwnerRepository.deleteById(menuId);
        }
    }
    
    //상점 삭제 처리 
    @Override
    public void modifyShopStat(Long shopId, Long shopDetailId, String infoType) {
        // infoType에 따라 분기처리 후
        // isExist() 값 false 변경 : 삭제처리
        Shop shop = shopRepository.findById(shopId).orElseThrow();

        if(infoType.equals("USER")){
            // ShopUser 정보 수정
            ShopUser shopUser = shopUserRepository.findById(shopDetailId).orElseThrow();
            shopUser.changeisExist(false);
            // Shop 정보 수정 : isUserData, isExist
            shop.changeUserData(false);
        }else if(infoType.equals("OWNER")){
            ShopOwner shopOwner = shopOwnerRepository.findById(shopDetailId).orElseThrow();
            shop.setEmail(null);
            shopOwner.setMember(null);
            // shopOwner에는 email이 null이면 안됨
            // 상점 삭제시, 사용자 email 정보를 가져와 shopOwner
            shop.changeCertificate(false);
            shopOwner.changeisExist(false); 
            shop.changeExist(false); // 사장님 점포 삭제시 존재여부 false로 변경
            shop.changeOwnerData(false);
        }

    }

    @Override
    public void updateShopExist(Long shopId) {
        Shop shop = shopRepository.findById(shopId).orElseThrow();
        // OwnerData , UserData가 둘 다 0이면 shop의 isExist를 false(0)으로 바꾼다.
        if(!shop.isOwnerData() && !shop.isUserData()) {
            shop.changeExist(false);
        }
    }

    @Override
    public ShopOwnerDTO getShopOwnerEmail(Long shopId) {
        log.info("shopId로 조회한 가게 Service: {}",shopId);
        ShopOwner shopOwner = shopOwnerRepository.findByShop_ShopId(shopId)
                .orElseThrow(() -> new EntityNotFoundException("ShopId로 ShopOwner 정보를 찾을 수 없습니다. : " + shopId));

        log.info("ShopId로 조회한 가게의 사장님 이메일 :{}",shopOwner.getMember().getEmail());
        return ShopOwnerDTO.builder()
                .email(shopOwner.getMember().getEmail())  // ShopOwner 엔티티의 Member에서 이메일 추출
                .build();
    }


}

