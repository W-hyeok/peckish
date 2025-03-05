package com.peckish.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long pno; // 상품 고유번호
    private String pname; // 상품명
    private int price; // 상품 가격
    private String pdesc; // 상품 설명
    private boolean delFlag; // 상품 삭제 여부

    // 업로드하는 파일. 수정 시 새로 추가되는 이미지 목록.
    @Builder.Default
    private List<MultipartFile> files = new ArrayList<MultipartFile>(); // ArrayList: List의 구현체. 내부적으로 배열을 사용하여 동적 크기 조정이 가능한 리스트

    // 업로드 완료된 파일명. 수정 시, 기존에 남겨둘 이미지 이름 목록.
    @Builder.Default
    private List<String> uploadFileNames = new ArrayList<>();
}

