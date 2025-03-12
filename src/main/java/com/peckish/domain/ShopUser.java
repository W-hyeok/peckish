package com.peckish.domain;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="SHOP_USER_ID")
    private Long shopUserId;

    // 상점 별점 평균
    @Setter
    private Double ratingAvg;

    private String title;
    private String location;
    private String days;
    private String category;
    private String openTime;
    private String closeTime;
    private boolean isOpen;
    private String filename;
    private boolean isExist; // 존재 여부 1 : 존재
    private LocalDateTime regDate;
    private LocalDateTime updateDate;
    @Builder.Default
    private String infoType = "USER";  // User

    // 메뉴
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "shopUser")
    @Builder.Default
    private List<MenuUser> menuUser = new ArrayList<>();

    // 리뷰
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "shopUser")
    @Builder.Default
    private List<ReviewUser> reviewUser = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY) // Member:Shop=N:1 // 기본값 EAGER
    @JoinColumn(name="email") // join 할(= 반대편의) 컬럼명
    @Setter
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="SHOP_ID")
    @Setter
    private Shop shop;

    //수정 가능한 정보들
    public void changeTitle(String title){
        this.title=title;
    }
    public void changeLocation(String location){
        this.location=location;
    }
    public void changeDays(String days) {
        this.days=days;
    }
    public void changeCategory(String category){
        this.category=category;
    }
    public void changeOpentime(String openTime){
        this.openTime=openTime;
    }
    public void changeCloseTime(String closeTime){
        this.closeTime=closeTime;
    }
    public void changeFileName(String filename) {
        this.filename=filename;
    }
    public void changeUpdateDate(LocalDateTime updateDate){
        this.updateDate=updateDate;
    }
    public void changeisExist(boolean isExist) {this.isExist = isExist;}
    public void changeIsOpen(boolean isOpen) {this.isOpen=isOpen;}



}

