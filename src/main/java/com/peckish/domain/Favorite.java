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
public class Favorite {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="FAVORTE_ID")
    private Long favoriteId;

    private LocalDateTime regDate;
    private LocalDateTime updateDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="email")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="SHOP_ID")
    private Shop shop;
}
