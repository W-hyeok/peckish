package com.peckish.dto;

import com.peckish.domain.Member;
import com.peckish.domain.ShopOwner;
import com.peckish.domain.ShopUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopDetailDTO {
    //private Long shopDetailId; // (PK) shopUser 또는 shopOwner 의 pk값이 담기는 변수
    private String email; // 작성자 email 정보
    private String title;
    private String location;
    private String days;
    private String category;
    private String openTime; // LocalDateTime?
    private String closeTime;
    private boolean isOpen;
    private MultipartFile shopfile; // 점포 사진 파일정보
    private String shopFilename; // (entity의 filename)점포 사진파일을 저정한 후 저장된 파일명 담아둘 변수
    private LocalDateTime regDate;
    private LocalDateTime updateDate;

    //DB에 저장하기 위해 Entity 변경
    // DTO -> OwnerEntity ( shop, menuOwner를 제외한 나머지 값 체우기)
    public ShopOwner toShopOwnerEntity() {
        ShopOwner owner = ShopOwner.builder()
                .title(title)
                .location(location)
                .days(days)
                .category(category)
                .openTime(openTime)
                .closeTime(closeTime)
                .isOpen(isOpen)
                .isExist(true)
                .filename(shopFilename)
                .regDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())

                .build();
        return owner;
    }
    // DTO -> UserEntity
    public ShopUser toShopUserEntity() {
        ShopUser user = ShopUser.builder()
                .title(title)
                .location(location)
                .days(days)
                .category(category)
                .openTime(openTime)
                .closeTime(closeTime)
                .isOpen(isOpen)
                .isExist(true)
                .filename(shopFilename)
                .regDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();
        return user;
    }

    //Entity -> DTO (ShopUser)
    public ShopDetailDTO toShopUserDTO(ShopUser shopUser){
        ShopDetailDTO shopDetailDTO = new ShopDetailDTO();

        shopDetailDTO.setTitle(shopUser.getTitle());
        shopDetailDTO.setLocation(shopUser.getLocation());
        shopDetailDTO.setShopFilename(shopUser.getFilename());
        shopDetailDTO.setOpenTime(shopUser.getOpenTime());
        shopDetailDTO.setCloseTime(shopUser.getCloseTime());
        shopDetailDTO.setCategory(shopUser.getCategory());
        shopDetailDTO.setDays(shopUser.getDays());

        return shopDetailDTO;
    }

    //Entity -> DTO (ShopOwner)
    public ShopDetailDTO toShopOwnerDTO(ShopOwner shopOwner){
        ShopDetailDTO shopDetailDTO = new ShopDetailDTO();

        shopDetailDTO.setTitle(shopOwner.getTitle());
        shopDetailDTO.setLocation(shopOwner.getLocation());
        shopDetailDTO.setShopFilename(shopOwner.getFilename());
        shopDetailDTO.setOpenTime(shopOwner.getOpenTime());
        shopDetailDTO.setCloseTime(shopOwner.getCloseTime());
        shopDetailDTO.setCategory(shopOwner.getCategory());
        shopDetailDTO.setDays(shopOwner.getDays());

        return shopDetailDTO;
    }





}
