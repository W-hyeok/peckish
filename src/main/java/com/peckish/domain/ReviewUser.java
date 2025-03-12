package com.peckish.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Review_USER_ID")
    private Long reviewUserId;

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
    @JoinColumn(name="SHOP_USER_ID")
    @Setter
    private ShopUser shopUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="email")
    @Setter
    private Member member;
}
