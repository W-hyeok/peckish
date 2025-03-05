package com.peckish.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImage {
    private String filename;
    @Setter
    private int ord; // 이미지마다 번호 지정, 대표 이미지 = 0
}
