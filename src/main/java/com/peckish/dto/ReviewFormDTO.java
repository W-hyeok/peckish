package com.peckish.dto;

import com.peckish.domain.ReviewOwner;
import com.peckish.domain.ReviewUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewFormDTO {

    //상점번호
    private Long shopId;
    //shopDetailId
    private Long shopDetailId;
    //infoType
    private String infoType;
    // 리뷰내용
    private String content;

    //DTO-> toReviewUserEntity
    public ReviewUser toReviewUserEntity() {
        ReviewUser reviewUser = ReviewUser.builder()
                .content(content)
                .build();
        return reviewUser;
    }

    //DTO -> toReviewOwnerEntity
    public ReviewOwner toReviewOwnerEntity() {
        ReviewOwner reviewOwner = ReviewOwner.builder()
                .content(content)
                .build();
        return reviewOwner;
    }


}
