package com.peckish.controller;

import java.util.Map;
import com.peckish.dto.ReviewFormDTO;
import com.peckish.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰등록
    @PostMapping("/add/{shopId}/{shopDetailId}/{infoType}")
    public Map<String, Long> addReview(@PathVariable("shopId") Long shopId,
                                         @PathVariable("shopDetailId") Long shopDetailId,
                                         @PathVariable("infoType") String infoType,
                                         ReviewFormDTO reviewFormDTO) {
        log.info("shopId : {}", shopId);
        log.info("shopDetailId : {}", shopDetailId);
        log.info("infoType : {}", infoType);
        log.info("ReviewDTO : {}", reviewFormDTO);

        reviewFormDTO.setShopId(shopId);
        reviewFormDTO.setShopDetailId(shopDetailId);
        reviewFormDTO.setInfoType(infoType);

        Long reviewId = reviewService.add(shopId, infoType, reviewFormDTO);

        return Map.of("result", reviewId);
    }


    // 리뷰 조회


    // 리뷰 수정


    // 리뷰 삭제


}
