package com.peckish.repository;

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

    // ShopId를 기준으로 ShopOwner 정보 조회
    Optional<ShopOwner> findByShop_ShopId(Long shopId);
}
