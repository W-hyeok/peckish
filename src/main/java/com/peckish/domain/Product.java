package com.peckish.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "imageList") // 연관관계 매핑 제외한 toString (로그 가독성 편의 - 임시 추가)
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pno;
    private String pname;
    private String pdesc;
    private int price;

    private boolean delFlag;

    @ElementCollection // 테이블 별도 생성 (default: lazy loading)
    @Builder.Default
    private List<ProductImage> imageList = new ArrayList<>(); // ArrayList: List의 구현체. 내부적으로 배열을 사용하여 동적 크기 조정이 가능한 리스트

    // 값 수정 메서드 (setter 미사용)
    public void changePrice(int price) {
        this.price = price;
    }
    public void changeDesc(String desc) {
        this.pdesc = desc;
    }
    public void changePname(String pname) {
        this.pname = pname;
    }

    // 이미지 추가: ProductImage 타입으로 추가
    public void addImage(ProductImage image) {
        image.setOrd(this.imageList.size()); // ord값 마지막 번호로 추가
        this.imageList.add(image);
    }

    // 파일명 문자열로 파일 추가
    public void addImageByFilename(String filename) {
        ProductImage productImage = ProductImage.builder().filename(filename).build();
        addImage(productImage);
    }

    // 이미지 삭제
    public void clearImageList() {
        this.imageList.clear();
    }

    // 상품 삭제 (직접 삭제 X, delFlag 변경)
    public void changeDelFlag(boolean delFlag) {
        this.delFlag = delFlag;
    }

}
