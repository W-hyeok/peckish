package com.peckish.repository;

import com.peckish.domain.Member;
import com.peckish.domain.Shop;
import com.peckish.domain.ShopUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, String> {
    // 연관된 Entity를 함께 로딩하는 방법을 지정 - "roleList"를 함께 로딩하여 N+1 문제 해결
    @EntityGraph(attributePaths = {"roleList"}) // Member 조회 시 연관시킬 경로
    @Query("SELECT m FROM Member m WHERE m.email = :email") // JPQL: Member(Entity)에서 email 기준으로 사용자 조회
    Member getMemberWithRoles(@Param("email") String email); // email 기준으로 Member 조회 + 연관된 roleList도 함께(left join) 조회


    @EntityGraph(attributePaths = {"roleList"}) // Member 조회 시 연관시킬 경로
    @Query("SELECT m FROM Member m WHERE m.phone = :phone") // JPQL: Member(Entity)에서 사용자 조회
    Member getMemberWithRolesByPhone(@Param("phone") String phone); // 연관된 roleList도 함께(left join) 조회


    // 회원 탈퇴 :  실질적으로 회원 정보를 삭제하는 것이 아니라 memberStat값만 0으로 변경하고 Login시 이 값 체크해서 login 불가로 처리
    // @Query: INSERT, UPDATE, DELETE 쿼리 사용 시 @Modifying 을 추가해야 함
    // 변경 감지(dirty checking)로 수정되는 게 아니며, 1차 캐시를 무시하고 바로 처리함
    @Modifying(clearAutomatically = true) // 영속성 컨텍스트 초기화
    @Query("UPDATE Member m SET m.memberStat = :memberStat WHERE m.email = :email")
    void updateToDeleteMember(@Param("email") String email, @Param("memberStat") int memberStat);

    Boolean existsByEmail(String email);
    Boolean existsByPhone(String phone);
    Boolean existsByBusinessNumber(String businessNumber);

    // shopList 정보 가져오기 구현
    @Query("SELECT su FROM ShopUser su WHERE su.shop.email = :email")
    List<ShopUser> findAllByEmail(@Param("email") String email);

    // memberList 정보 가져오기 구현
    @Query("SELECT m FROM Member m WHERE m.businessNumber = :businessNumber")
    List<Member> findAllByBusinessNumber(@Param("businessNumber") String businessNumber);

    // shop 정보 가져오기 구현 중
    @Query("SELECT s FROM Shop s WHERE s.email = :email")
    Shop findOneShopByEmail(@Param("email") String email);

    // 영업개시 쿼리문 작성
    @Modifying(clearAutomatically = true)
    @Query("UPDATE ShopOwner so SET so.isOpen = true WHERE so.shop.email = :email")
    void openShopAtRepository(@Param("email") String email);

    // 영업종료 쿼리문 작성
    @Modifying(clearAutomatically = true)
    @Query("UPDATE ShopOwner so SET so.isOpen = false WHERE so.shop.email = :email")
    void closeShopAtRepository(@Param("email") String email);

    @EntityGraph(attributePaths = {"roleList"}) // Member 조회 시 연관시킬 경로
    @Query("SELECT m FROM Member m WHERE m.businessNumber = :businessNumber") // JPQL: Member(Entity)에서 사용자 조회
    Member getMemberWithRolesByBusinessNumber(String businessNumber); // 연관된 roleList도 함께(left join) 조회
}
