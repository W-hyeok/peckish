package com.peckish.dto;

import com.peckish.domain.ShopOwner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopOwnerDTO {
    private Long shopOwnerId; //Owner 테이블에서 pk

    private Long shopId; //상점번호
    private String email;
    private String title;
    private String location;
    private String days;
    private String category;
    private String openTime;
    private String closeTime;
    private boolean isOpen;
    private String filename;
    private boolean isExist; //삭제여부 0: false/1:true
    private LocalDateTime regDate;
    private LocalDateTime updateDate;

    // 생성자 : Entity -> DTO로 변환+생성
    public ShopOwnerDTO(ShopOwner shopOwner) {
        this.shopOwnerId = shopOwner.getShopOwnerId();
        // 추후 email이 화면에서 필요하면 repository EntityGraph에 member도 추가해서
        // member까지 같이 조회해와 아래 코드로 이메일 추가하기
        //this.email = shopUser.getMember().getEmail();
        this.title = shopOwner.getTitle();
        this.location = shopOwner.getLocation();
        this.category = shopOwner.getCategory();
        this.days = shopOwner.getDays();
        this.openTime = shopOwner.getOpenTime();
        this.closeTime = shopOwner.getCloseTime();
        this.isOpen = shopOwner.isOpen();
        this.isExist = shopOwner.isExist();
        this.filename = shopOwner.getFilename();
        this.updateDate = shopOwner.getUpdateDate();
    }


}
