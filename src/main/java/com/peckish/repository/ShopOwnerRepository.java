package com.peckish.repository;

import com.peckish.domain.ReviewOwner;
import com.peckish.domain.ShopOwner;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ShopOwnerRepository extends JpaRepository<ShopOwner, Long> {

    // join시 필요 (조인할 entity명)
    @EntityGraph(attributePaths = {"menuOwner"})
    @Query("select owner from ShopOwner owner where owner.shop.shopId = :shopId")
    // shopId를 기준으로 ShopOwner + menuOwner 조인하여 가져와라
    public ShopOwner selectShopOwnerByShopUserId(@Param("shopId") Long shopId);

    // shopOwner를 기준으로 reviewOwner(필드명) 조인해옴
    @EntityGraph(attributePaths = {"reviewOwner"})
    @Query("select owner from ShopOwner owner where owner.shop.shopId=:shopId")
    // public ShopOwner(반환타입)
    public ShopOwner selectReviewOwnerByShopId(@Param("shopId") Long shopId);

    // ShopId를 기준으로 ShopOwner 정보 조회
    Optional<ShopOwner> findByShop_ShopId(Long shopId);


    // shop 정보 가져오기 구현 중
    @EntityGraph(attributePaths = {"shop"})
    @Query("SELECT so FROM ShopOwner so WHERE so.member.email = :email")
    ShopOwner findOneShopByEmailFromShopOwner(@Param("email") String email);

}
