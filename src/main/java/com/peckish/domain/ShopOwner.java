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
public class ShopOwner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="SHOP_OWNER_ID") // join 대상 컬럼명 지정
    private Long shopOwnerId;

    private String title;
    private String location;
    private String days;
    private String category;
    private String openTime;
    private String closeTime;
    private boolean isOpen;
    private String filename;
    private boolean isExist; // 삭제 여부 (1: 삭제처리)
    private LocalDateTime regDate;
    private LocalDateTime updateDate;
    @Builder.Default
    private String infoType = "OWNER";  // OWNER - owner

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="SHOP_ID")
    @Setter
    private Shop shop;

    @OneToOne(fetch = FetchType.LAZY) // Member:Shop=N:1 // 기본값 EAGER
    @JoinColumn(name="email") // join 할(= 반대편의) 컬럼명
    @Setter
    private Member member;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "shopOwner")
    private List<MenuOwner> menuOwner = new ArrayList<>();

    // 수정 : regDate 제외한 모든 데이터 수정
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
    public void changeFileName(String filename) {this.filename=filename;}
    public void changeUpdateDate(LocalDateTime updateDate){
        this.updateDate=updateDate;
    }
    public void changeisExist(boolean isExist) {this.isExist=isExist;}
    public void changeIsOpen(boolean isOpen) {this.isOpen=isOpen;}

}
