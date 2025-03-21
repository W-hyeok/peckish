package com.peckish.dto;

import com.peckish.domain.ShopUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopUserDTO {
    private Long shopUserId; //pk

    private Long shopId; //상점번호
    private boolean isExist; //삭제 여부
    private String email;
    private String title;
    private String location;
    private String days;
    private String category;
    private String openTime;
    private String closeTime;
    private boolean isOpen;
    private String filename;
    private LocalDateTime regDate;
    private LocalDateTime updateDate;

    // 생성자 : Entity -> DTO로 변환+생성
    public ShopUserDTO(ShopUser shopUser) {
        this.shopUserId = shopUser.getShopUserId();
        // 추후 email이 화면에서 필요하면 repository EntityGraph에 member도 추가해서
        // member까지 같이 조회해와 아래 코드로 이메일 추가하기
        this.email = shopUser.getMember().getEmail();
        this.title = shopUser.getTitle();
        this.location = shopUser.getLocation();
        this.category = shopUser.getCategory();
        this.days = shopUser.getDays();
        this.openTime = shopUser.getOpenTime();
        this.closeTime = shopUser.getCloseTime();
        this.isOpen = shopUser.isOpen();
        this.filename = shopUser.getFilename();
        this.isExist = shopUser.isExist();
        this.updateDate = shopUser.getUpdateDate();
    }

}
