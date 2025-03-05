package com.peckish.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// 상점 Detail 정보 최종적으로 화면에 전달하는 용도의 DTO
@Data
@Builder
@AllArgsConstructor // 모든 변수를 매개변수로 갖는 생성자 자동생성
@NoArgsConstructor // 매개변수없는 생성자 자동으로 생성
// @RequiredArgsConstructor // 필요한 매개변수만 생성자 생성
public class ShopDetailRespDTO {

    // shop
    //@NonNull
    private ShopDTO shopDTO;
    // shopOwner + menu
    private ShopOwnerDTO shopOwnerDTO;
    private List<MenuRespDTO> menuOwnerList;
    // shopUser + menu
    private ShopUserDTO shopUserDTO;
    private List<MenuRespDTO> menuUserList;


}
