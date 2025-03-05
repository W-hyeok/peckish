package com.peckish.dto;

import com.peckish.domain.Shop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopDTO {
    private Long shopId; //상점 번호
    private boolean isExist; //삭제 여부
    private boolean certificate; //제보/인증 분기 (화면에서 넘어오는 정보는 이 클래스에서 이것 하나)

    private String email;

    private boolean isOwnerData; // 사장 데이터가 있는지 여부
    private boolean isUserData; // 제보 데이터가 있는지 여부

    private ShopOwnerDTO shopOwnerDTO;
    private ShopUserDTO shopUserDTO;

    private LocalDateTime regDate;
    private LocalDateTime updateDate;


    // DTO -> Entity 변환 메서드
    public Shop toEntity() {
        Shop shop = Shop.builder()
                .certificate(certificate)
                .isExist(true)
                .email(email)
                .isOwnerData(isOwnerData) //true 사장정보
                .isUserData(isUserData) //true 유저정보
                .regDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();
        return shop;
    }
    //ShopDTO 생성자 : ShopEntity -> ShopDTO
    public ShopDTO(Shop shop){
        this.shopId = shop.getShopId();
        this.isExist = shop.isExist();
        this.email = shop.getEmail();
        this.certificate = shop.isCertificate();
        this.isOwnerData= shop.isOwnerData(); // 1 : true
        this.isUserData = shop.isUserData();
        this.regDate = shop.getRegDate();
        this.updateDate = shop.getUpdateDate();
                
    }



}

