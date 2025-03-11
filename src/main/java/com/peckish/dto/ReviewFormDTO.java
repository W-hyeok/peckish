package com.peckish.dto;

import com.peckish.domain.ReviewOwner;
import com.peckish.domain.ReviewUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewFormDTO {

    // 상점번호
    private Long shopId;
    // shopDetailId - user, owner의 id
    private Long shopDetailId;
    // infoType
    private String infoType;
    // 리뷰내용
    private String content;
    // 별점
    private Double rating;
    // 작성자
    private String email;

    private LocalDateTime regDate;
    private LocalDateTime updateDate;

    //DTO-> toReviewUserEntity
    public ReviewUser toReviewUserEntity() {
        ReviewUser reviewUser = ReviewUser.builder()
                .content(content)
                .rating(rating)
                .updateDate(updateDate)
                .regDate(regDate)
                .build();
        return reviewUser;
    }

    //DTO -> toReviewOwnerEntity
    public ReviewOwner toReviewOwnerEntity() {
        ReviewOwner reviewOwner = ReviewOwner.builder()
                .content(content)
                .rating(rating)
                .updateDate(updateDate)
                .regDate(regDate)
                .build();
        return reviewOwner;
    }


}
