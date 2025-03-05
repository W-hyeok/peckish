package com.peckish.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuOwner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="MENU_OWNER_ID")
    private Long menuOwnerId; //a_i_

    private String menuName;
    private String price;
    private String menuFilename;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="SHOP_OWNER_ID")
    @Setter
    private ShopOwner shopOwner;

    private LocalDateTime regDate;
    private LocalDateTime updateDate;



}
