package com.peckish.dto;


import com.peckish.domain.ShopUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopMoonDTO {
    private Long shopId;
    private boolean isExist; //삭제 여부
    private String email;
    private String title;
    private String location;
    private String category;
    private boolean isOpen;
    private String filename;

    // 생성자 : Entity -> DTO로 변환+생성
    public ShopMoonDTO(ShopUser shopUser) {
        this.shopId = shopUser.getShop().getShopId();
        this.email = shopUser.getShop().getEmail();
        this.title = shopUser.getTitle();
        this.location = shopUser.getLocation();
        this.category = shopUser.getCategory();
        this.isOpen = shopUser.isOpen();
        this.filename = shopUser.getFilename();
    }

}
