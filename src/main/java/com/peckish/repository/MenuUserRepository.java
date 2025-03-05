package com.peckish.repository;

import com.peckish.domain.MenuUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MenuUserRepository extends JpaRepository<MenuUser, Long> {

    // USER 타입의 메뉴 정보 조회
   // Optional<MenuUser> findByShopIdAndType(Long shopId, String type);

    // shopUserId로 메뉴 가져오기
    @Query("select menu from MenuUser menu where menu.shopUser.shopUserId = :shopUserId")
    public List<MenuUser> selectMenuUserByShopUserId(@Param("shopUserId") Long shopUserId);

}

