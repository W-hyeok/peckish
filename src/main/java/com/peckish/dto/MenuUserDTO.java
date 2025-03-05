package com.peckish.dto;

import com.peckish.domain.MenuUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuUserDTO {
    private Long menuUserId; //UserMenu 테이블에서 pk 고유번호

    //private boolean isExist; //삭제 여부
    private String menuName;
    private String price;
    private String menuFilename;
    //private ShopUser shopUser;
    private LocalDateTime regDate;
    private LocalDateTime updateDate;

    // 생성자
    //Entity -> DTO toMenuUserDTO = DB에서 조회한 내용을 DTO로 변환 -> 화면에 전달 목적 큼
    public MenuUserDTO (MenuUser menuUser) {

        this.menuUserId = menuUser.getMenuUserId();
        this.menuName = menuUser.getMenuName();
        this.price = menuUser.getPrice();
        this.menuFilename= menuUser.getMenuFilename();
        this.updateDate = menuUser.getUpdateDate();

    }




}
