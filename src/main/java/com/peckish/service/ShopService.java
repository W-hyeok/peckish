package com.peckish.service;

import com.peckish.dto.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface ShopService {

    //상점 등록
    Long add(ShopDTO shopDTO, ShopDetailDTO shopDetailDTO, MapDTO mapDTO); //상점 등록

    //상점 디테일 등록
    Long addDetail(Long shopId, ShopDTO shopDTO, ShopDetailDTO shopDetailDTO, MapDTO mapDTO);

    // 제보 정보 추가 등록
    Long addShopUser(Long shopId, ShopDTO shopDTO, ShopDetailDTO shopDetailDTO, MapDTO mapDTO);

    // 인증 정보 추가 등록
    Long addShopOwner(Long shopId, ShopDTO shopDTO, ShopDetailDTO shopDetailDTO, MapDTO mapDTO);

    //메뉴 등록
    Long menuAdd(MenuFormDTO menuFormDTO);

    // shopp + shopUser + shopOwner 정보 가져오기
    ShopDTO getShop(Long shopId);

    // 메뉴 User목록 조회
    List<MenuRespDTO> getShopUserMenu(Long shopId);

    // 메뉴 Owner목록 조회
    List<MenuRespDTO> getShopOwnerMenu(Long shopId);

    // 상점 수정
    void modifyShop(Long shopId, String infoType, Long shopDetailId, ShopDetailDTO shopDetailDTO);

    // 메뉴 삭제
    void deleteMenu(Long menuId, String infoType);

    // 상점 삭제
    void modifyShopStat(Long shopId, Long shopDetailId,String infoType);

    // 상점 exist 업데이트
    void updateShopExist(Long shopId);

    // 상점 주인 이메일 조회
    ShopOwnerDTO getShopOwnerEmail(Long shopId);


}
