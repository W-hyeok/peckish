package com.peckish.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Review_Owner_Id")
    private Long reviewOwnerId;

    private String content;

    private LocalDateTime regDate;
    private LocalDateTime updateDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="shop_id")
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="SHOP_OWNER_ID")
    private ShopOwner shopOwner;
}
