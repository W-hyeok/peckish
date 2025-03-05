package com.peckish.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private String content;

    private LocalDateTime regDate;
    private LocalDateTime updateDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="shop_id")
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="SHOP_USER_ID")
    private ShopUser shopUser;
}
