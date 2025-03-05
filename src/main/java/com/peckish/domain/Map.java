package com.peckish.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Map {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MAP_ID")
    private Long mapId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHOP_ID")
    @Setter
    private Shop shop;

    private String title; // 제목
    private double lat; // 위도
    private double lng; // 경도

}
