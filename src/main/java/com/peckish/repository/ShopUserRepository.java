package com.peckish.repository;

import com.peckish.domain.ReviewUser;
import com.peckish.domain.ShopUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

//Entity별 Respository 만들어야 함 / JpaRespository<entity명, PK타입>
public interface ShopUserRepository extends JpaRepository<ShopUser, Long> {

    // shopUserId로 ShopUser정보와 해당 Menu들 모두 한번에 조회해오는 메서드 (3줄이 하나임)
    @EntityGraph(attributePaths = {"menuUser"})
    @Query("select user from ShopUser user where user.shop.shopId = :shopId")
    public ShopUser selectShopUserByShopId(@Param("shopId") Long shopId);

    @EntityGraph(attributePaths = {"reviewUser"})
    @Query("select user from ShopUser user where user.shop.shopId = :shopId")
    public ShopUser selectReviewUserByShopId(@Param("shopId") Long shopId);
}
