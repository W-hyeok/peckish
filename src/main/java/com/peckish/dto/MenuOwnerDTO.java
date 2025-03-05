package com.peckish.dto;

import com.peckish.domain.MenuOwner;
import com.peckish.domain.ShopOwner;
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
public class MenuOwnerDTO {
    private Long menuOwnerId; //OwnerMenu 테이블에서 pk 고유번호

    private boolean isExist; //삭제 여부
    private String menuName;
    private String price;
    private MultipartFile menuFile;
    private String menuFilename;
    private ShopOwner shopOwner;
    private LocalDateTime regDate;
    private LocalDateTime updateDate;

/*
    //DTO -> MenuOwnerEntity
    public MenuOwner toMenuOwnerEntity() {
        MenuOwner menuOwner = MenuOwner.builder()
                .menuName(menuName)
                .price(price)
                .active(true)
                .regDate(regDate)
                .updateDate(updateDate)
                .build();
        return menuOwner;


    }

 */

    //Entity -> DTO toMenuUserDTO = DB에서 조회한 내용을 DTO로 변환 -> 화면에 전달 목적 큼
    public MenuOwnerDTO (MenuOwner menuOwner) {

        this.menuOwnerId = menuOwner.getMenuOwnerId();
        this.menuName = menuOwner.getMenuName();
        this.price = menuOwner.getPrice();
        this.menuFilename= menuOwner.getMenuFilename();

    }



}
