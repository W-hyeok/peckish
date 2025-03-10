package com.peckish.repository;

import com.peckish.domain.Map;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MapRepository extends JpaRepository<Map, Long> {

    // 맵 하나 조회 (게시글 조회 시 위도 경도값 가져오기 위함)
    @Query("SELECT m FROM Map m " +
            "JOIN FETCH m.shop s " +
            "LEFT JOIN FETCH m.shop.shopUser su " +
            "LEFT JOIN FETCH m.shop.shopOwner so " +
            "WHERE s.shopId = :sid " +
            "AND (su.isExist = true OR so.isExist = true)")
    Map getMap(@Param("sid") int sid);

    // 카테고리 없음 + 구분(인증/제보) 없음
    // 기본 메서드(findAll() 사용) + isExist 조건 추가
    @Query("SELECT m FROM Map m " +
            "JOIN FETCH m.shop s " +
            "LEFT JOIN FETCH m.shop.shopUser su " +
            "LEFT JOIN FETCH m.shop.shopOwner so " +
            "WHERE so.isExist = true OR su.isExist = true")
    List<Map> findAll();

//    // 카테고리 없음 + 영업 중
//    @Query("SELECT m from Map m JOIN FETCH m.shop s " +
//            "LEFT JOIN FETCH m.shop.shopUser su LEFT JOIN FETCH m.shop.shopOwner so " +
//            "WHERE so.isOpen = :open " +
//            "OR su.isOpen = :open " +
//            "AND (su.isExist = true OR so.isExist = true)")
//    List<Map> selectAllByOpen(@Param("open") boolean open);

    // 카테고리 있음 + 구분 없음
    // @EntityGraph(attributePaths = {"shop"}) 필요 없음...?
    // 성능 최적화를 위해 JOIN FETCH 사용
    @Query("SELECT m FROM Map m " +
            "JOIN FETCH m.shop s " +
            "LEFT JOIN FETCH m.shop.shopUser su " +
            "LEFT JOIN FETCH m.shop.shopOwner so " +
            "WHERE so.category = :category " +
            "OR su.category = :category " +
            "AND (su.isExist = true OR so.isExist = true)")
    List<Map> findAllByCate(@Param("category") String category);

//    // 카테고리 없음 + 제보된 것만 조회
//    //    @EntityGraph(attributePaths = {"shop", "shop.shopUser"})
//    @Query("SELECT m FROM Map m JOIN FETCH m.shop s LEFT JOIN FETCH s.shopUser su " +
//            "WHERE s.certificate = :certificate " +
//            "AND su.isExist = true")
//    List<Map> findAllByUser(@Param("certificate") boolean certificate);
//
//    // 카테고리 없음 + "제보" + "영업 중"
//    @Query("SELECT m FROM Map m JOIN FETCH m.shop s LEFT JOIN FETCH s.shopUser su " +
//            "WHERE su.isOpen = :open " +
//            "AND s.certificate = :certificate " +
//            "AND su.isExist = true")
//    List<Map> findAllByUserWithOpen(@Param("certificate") boolean certificate,
//                                    @Param("open") boolean open);
//
//    // 카테고리 없음 + "인증" + "영업 중"
//    @Query("SELECT m FROM Map m JOIN FETCH m.shop s LEFT JOIN FETCH s.shopOwner so " +
//            "WHERE so.isOpen = :open " +
//            "AND s.certificate = :certificate " +
//            "AND so.isExist = true")
//    List<Map> findAllByOwnerWithOpen(@Param("certificate") boolean certificate, @Param("open") boolean open);
//
//    // 카테고리 없음 + 인증된 것만 조회
//    @EntityGraph(attributePaths = {"shop", "shop.shopOwner"})
//    @Query("SELECT m FROM Map m LEFT JOIN m.shop s LEFT JOIN s.shopOwner so " +
//            "WHERE s.certificate = :certificate " +
//            "AND so.isExist = true")
//    List<Map> findAllByOwner(@Param("certificate") boolean certificate);
//
//    // 카테고리 있음 + 제보된 것만 조회
//    @EntityGraph(attributePaths = {"shop", "shop.shopUser"})
//    @Query("SELECT m FROM Map m " +
//            "JOIN FETCH m.shop s LEFT JOIN FETCH s.shopUser su " +
//            "WHERE su.category = :category " +
//            "AND s.certificate = :certificate " +
//            "AND su.isExist = true")
//    List<Map> findByCategoryWithUser(@Param("category") String category,
//                                     @Param("certificate") boolean certificate);
//
//    // 카테고리 있음 + 인증된 것만 조회
//    @EntityGraph(attributePaths = {"shop", "shop.shopOwner"})
//    @Query("SELECT m FROM Map m " +
//            "JOIN FETCH m.shop s LEFT JOIN FETCH s.shopOwner so " +
//            "WHERE so.category = :category " +
//            "AND s.certificate = :certificate " +
//            "AND so.isExist = true")
//    List<Map> findByCategoryWithOwner(@Param("category") String category,
//                                      @Param("certificate") boolean certificate);
//
//    // 카테고리 있음 + 영업 중만 조회 (동일한 내용의 조건 여러개니까 소괄호로 묶어야 함)
//    @Query("SELECT m FROM Map m JOIN FETCH m.shop s " +
//            "LEFT JOIN FETCH m.shop.shopUser su LEFT JOIN FETCH m.shop.shopOwner so " +
//            "WHERE (su.category = :category OR so.category = :category) " +
//            "AND (su.isOpen = :open OR so.isOpen = :open) " +
//            "AND (su.isExist = true OR so.isExist = true)")
//    List<Map> findByCateWithOpen(@Param("category") String category,
//                                 @Param("open") boolean open);
//
//    // 카테고리 있음 + 제보된 것만 조회 + 영업 중만 조회
//    @Query("SELECT DISTINCT m FROM Map m " +
//            "JOIN FETCH m.shop s LEFT JOIN FETCH s.shopUser su " +
//            "WHERE su.category = :category " +
//            "AND s.certificate = :certificate " +
//            "AND su.isOpen = :open " +
//            "AND su.isExist = true")
//    List<Map> findByCateWithUserAndOpen(@Param("category") String category,
//                                        @Param("certificate") boolean certificate,
//                                        @Param("open") boolean open);
//
//    // 카테고리 있음 + 인증된 것만 조회 + 영업 중만 조회
//    @Query("SELECT m FROM Map m " +
//            "JOIN FETCH m.shop s LEFT JOIN FETCH s.shopOwner so " +
//            "WHERE so.category = :category " +
//            "AND s.certificate = :certificate " +
//            "AND so.isOpen = :open " +
//            "AND so.isExist = true")
//    List<Map> findByCateWithOwnerAndOpen(@Param("category") String category,
//                                         @Param("certificate") boolean certificate,
//                                         @Param("open") boolean open);
//
//    // 테스트용 (fetch)
//    @EntityGraph(attributePaths = {"shop"})
//    @Query("SELECT m FROM Map m")
//    List<Map> testFind();



}
