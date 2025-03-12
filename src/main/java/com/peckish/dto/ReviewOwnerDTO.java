package com.peckish.dto;

import com.peckish.domain.ReviewOwner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewOwnerDTO {

    private Long reviewOwnerId; //pk

    private String email;
    private Double rating;
    private String content;

    private LocalDateTime regDate;
    private LocalDateTime updateDate;



}
