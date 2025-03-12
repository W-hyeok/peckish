package com.peckish.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Review_Owner_Id")
    private Long reviewOwnerId;

    //내용
    private String content;
    // 별점
    private Double rating;

    @Setter
    private LocalDateTime regDate;
    @Setter
    private LocalDateTime updateDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="shop_id")
    @Setter
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="SHOP_OWNER_ID")
    @Setter
    private ShopOwner shopOwner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="email")
    @Setter
    private Member member;
}
