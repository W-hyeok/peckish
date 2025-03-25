package com.peckish.repository;

import com.peckish.domain.MenuOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface MenuOwnerRepository extends JpaRepository<MenuOwner, Long> {
    // OWNER 타입의 메뉴 정보 조회
   // Optional<MenuOwner> findByShopIdAndType(Long shopId, String type);


}
