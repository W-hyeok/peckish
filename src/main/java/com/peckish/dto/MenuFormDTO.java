package com.peckish.dto;

import com.peckish.domain.MenuOwner;
import com.peckish.domain.MenuUser;
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
public class MenuFormDTO {

    private Long shopId; // 상점번호 : 주소에서 뽑아서 담기
    private String infoType; //OWNER, USER  : 주소에서 뽑아서 담기

    private Long shopDetailId; // shop_owner or shop_user 테이블의 id값 = formData에서 넘어옴
    private String menuName;
    private String price;
    private MultipartFile menuFile; // formData에서 실제 파일정보 넘어옴

    private String menuFilename; // 파일 저장할 때 이름 : 컨트롤러에서 파일 저정후 여기에 체워서 서비스에 보내기


    //DTO -> Entity toMenuOwnerEntity
    public MenuOwner toMenuOwnerEntity() {
        MenuOwner menuOwner = MenuOwner.builder()
                .menuName(menuName)
                .price(price)
                .menuFilename(menuFilename)
                .regDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();
        return menuOwner;
    }

    //DTO -> Entity toMenuUserEntity
    public MenuUser toMenuUserEntity() {
        MenuUser menuUser = MenuUser.builder()
                .menuName(menuName)
                .price(price)
                .menuFilename(menuFilename)
                .regDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();
        return menuUser;
    }


    //Entity -> DTO toMenuOwnerDTO
    public MenuOwnerDTO toMenuOwnerDTO(MenuOwner menuOwner){
        MenuOwnerDTO menuOwnerDTO = new MenuOwnerDTO();

        menuOwnerDTO.setMenuName(menuOwner.getMenuName());
        menuOwnerDTO.setPrice(menuOwner.getPrice());
        menuOwnerDTO.setMenuFilename(menuOwner.getMenuFilename());

        return menuOwnerDTO;
    }

}
// 화면에서 메뉴 등록시 넘어오는 데이터 담아주는 폼용 DTO
