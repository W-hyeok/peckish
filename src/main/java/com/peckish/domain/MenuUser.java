package com.peckish.domain;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="MENU_USER_ID")
    private Long menuUserId;  //a_i_

    private String menuName;
    private String price;
    private String menuFilename;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="SHOP_USER_ID")
    @Setter
    private ShopUser shopUser;

    private LocalDateTime regDate;
    private LocalDateTime updateDate;
}
