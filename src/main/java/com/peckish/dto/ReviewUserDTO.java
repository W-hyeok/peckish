package com.peckish.dto;

import com.peckish.domain.Member;
import com.peckish.domain.ReviewUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewUserDTO {

    private Long reviewUserId; //pk
    private String email; // 작성자

    private Double rating;
    private String content;
    private LocalDateTime regDate;
    private LocalDateTime updateDate;

    //DTO -> Entity
    private ReviewUser toEntity(ReviewUserDTO reviewUserDTO) {
        ReviewUser reviewUser = ReviewUser.builder()
                .rating(rating)
                .content(content)
                .regDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();
        return reviewUser;
    }



}
