package com.peckish.repository;

import com.peckish.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 상품 조회 (fetch join)
    @EntityGraph(attributePaths = {"imageList"}) // 연관관계가 걸려있는 Entity의 변수명
    @Query("SELECT p FROM Product p WHERE p.pno = :pno")
    Optional<Product> selectOne(@Param("pno") Long pno);

    // 상품 삭제(효과)
    // @Query: INSERT, UPDATE, DELETE 쿼리 사용 시 @Modifying 을 추가해야 함
    // 변경 감지(dirty checking)로 수정되는 게 아니며, 1차 캐시를 무시하고 바로 처리함
    @Modifying(clearAutomatically = true) // 영속성 컨텍스트 초기화
    @Query("UPDATE Product p SET p.delFlag = :delFlag WHERE p.pno = :pno")
    void updateToDelete(@Param("pno") Long pno, @Param("delFlag") boolean delFlag);

    // 상품 목록 조회: left join
    @Query("SELECT p, pi FROM Product p LEFT JOIN p.imageList pi WHERE pi.ord=0 AND p.delFlag = false")
    Page<Object[]> selectList(Pageable pageable);
}
