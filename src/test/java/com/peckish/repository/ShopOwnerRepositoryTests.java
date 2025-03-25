package com.peckish.repository;

import com.peckish.domain.Shop;
import com.peckish.domain.ShopOwner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ShopOwnerRepositoryTests {

    @Autowired
    ShopOwnerRepository shopOwnerRepository;
    @Autowired
    ShopRepository shopRepository;

    @Test
    public void getOwnerShop() {
        ShopOwner findShopOwner = shopOwnerRepository.findOneShopByEmailFromShopOwner("asd@test.com");
        Long shopId = findShopOwner.getShop().getShopId();
        Shop findShop = shopRepository.findById(shopId).orElse(null);
        System.out.println(findShop);
        System.out.println(findShop.getShopOwner());
        System.out.println(findShop.getShopUser());
    }


}