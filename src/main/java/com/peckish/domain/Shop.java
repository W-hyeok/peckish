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
// @ToString(exclude = "member")
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SHOP_ID") // db 컬럼명 shop_id로 지정
    private Long shopId;

    private boolean isExist; // 삭제 여부 (true = 1: 존재, false = 0: 삭제처리)

    @Setter
    private String email;

    private boolean certificate; // 폼입력시  제보/인증 분기 (제보:0 false, 사장:1 true)

    private boolean isOwnerData; // 사장 데이터가 있는지 여부
    private boolean isUserData; // 제보 데이터가 있는지 여부

    private LocalDateTime regDate;
    private LocalDateTime updateDate;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "shop")
    @JoinColumn(name="SHOP_OWNER_ID")
    private ShopOwner shopOwner;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "shop")
    @JoinColumn(name="SHOP_USER_ID")
    private ShopUser shopUser;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "shop")
    @JoinColumn(name="MAP_ID")
    private Map map;

    @OneToMany(mappedBy = "shop", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Visited> visited = new ArrayList<>();

    @OneToMany(mappedBy = "shop", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Favorite> favorites = new ArrayList<>();

    @OneToMany(mappedBy = "shop", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();


    // 수정 : isExist, isOwner, isUser, updateDate
    public void changeExist(boolean isExist) {
        this.isExist = isExist;
    }
    public void changeOwnerData(boolean isOwnerData) {
        this.isOwnerData = isOwnerData;
    }
    public void changeUserData(boolean isUserData) {
        this.isUserData = isUserData;
    }
    public void changeCertificate(boolean certificate){this.certificate = certificate;}
    public void changeUpdateDate() {
        this.updateDate = LocalDateTime.now();
    }


}
