package com.peckish.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {
    // 기본값 1/10으로 설정 (데이터 있으면 그걸로, 없으면 1/10)
    @Builder.Default //  Builder로 특정 필드 초기값 설정 시 부착하는 어노테이션
    private int page = 1;
    @Builder.Default // 필드마다 부착해야 함
    private int size = 12;
}
