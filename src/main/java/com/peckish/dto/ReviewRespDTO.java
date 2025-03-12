package com.peckish.dto;

import com.peckish.domain.ReviewOwner;
import com.peckish.domain.ReviewUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRespDTO {

    // 리뷰 목록 컴포넌트에 뿌려질 부분
    private Long reviewId; // ReviewUser와 ReviewOwner pk
    private Double rating; // 별점
    private Double ratingAvg; // 별점 평균
    private String content; // 리뷰내용
    private String email; // Member 작성자
    private String profileFilename;  // 작성자 프로필 사진
    private LocalDateTime regDate; // 최초 작성일자
    private LocalDateTime updateDate; // 수정 일자

    // ReviewUser Entity -> DTO
    public ReviewRespDTO(ReviewUser reviewUser){
        this.reviewId = reviewUser.getReviewUserId();
        this.rating = reviewUser.getRating();
        this.ratingAvg=reviewUser.getShopUser().getRatingAvg();
        this.content = reviewUser.getContent();
        this.email = reviewUser.getMember().getEmail();
        this.profileFilename = reviewUser.getMember().getProfileFilename();
        this.regDate = reviewUser.getRegDate();
        this.updateDate = reviewUser.getUpdateDate();
    }

    // ReviewOwner Entity -> DTO
    public ReviewRespDTO(ReviewOwner reviewOwner) {
        this.reviewId = reviewOwner.getReviewOwnerId();
        this.rating = reviewOwner.getRating();
        this.ratingAvg = reviewOwner.getShopOwner().getRatingAvg();
        this.content = reviewOwner.getContent();
        this.email = reviewOwner.getMember().getEmail();
        this.profileFilename = reviewOwner.getMember().getProfileFilename();
        this.regDate = reviewOwner.getRegDate();
        this.updateDate = reviewOwner.getUpdateDate();
    }


}
