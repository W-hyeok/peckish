package com.peckish.controller;

import java.util.List;
import java.util.Map;

import com.peckish.domain.ReviewUser;
import com.peckish.dto.ReviewFormDTO;
import com.peckish.repository.ReviewUserRepository;
import com.peckish.service.ReviewService;
import com.peckish.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewUserRepository reviewUserRepository;

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
    @GetMapping("/get/{shopId}/{infoType}")
    public Map<String, List> getReview(@PathVariable("shopId") Long shopId,
                                       @PathVariable("infoType") String infoType) {
        log.info("getReview - shopId : {}", shopId);
        log.info("getReview - infoType : {}", infoType);

        List reviewList = null;
        if (infoType.equals("USER")) {
            reviewList = reviewService.getReviewUser(shopId);

        } else if (infoType.equals("OWNER")) {
            reviewList = reviewService.getReviewOwner(shopId);
        }
        return Map.of("Result", reviewList);
    }

        // User - 리뷰 별점 계산
        @GetMapping("/average/{shopId}/USER")
        public Double UserRatingAvg(@PathVariable("shopId") Long shopId) {
            log.info("USER ratingAvg - shopId : {}", shopId);

            Double average = reviewService.updateShopUserRating(shopId);

            if (average != null) {
                average = Math.round(average * 100.0) / 100.0;
            } else {
                average = 0.0; // null 값 방지
            }
            return average; //
        }

        // Owner - 리뷰 별점 계산
        @GetMapping("/average/{shopId}/OWNER")
        public Double OwnerRatingAvg(@PathVariable("shopId") Long shopId) {
            log.info("shopId - OWNER ratingAvg : {}", shopId);

            Double average = reviewService.findOwnerRatingAvg(shopId);

            if (average != null) {
                average = Math.round(average * 100.0) / 100.0;
            } else {
                average = 0.0; // null 값 방지
            }
            return average; //
        }









            // 리뷰 별점 삭제시에도 자동으로 별점 계산
//    @GetMapping("/average/{reviewId}")
//    public void deleteReview(@PathVariable("reviewId") Long reviewId){
//        ReviewUser reviewUser = reviewUserRepository.findById(reviewId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰 없음"));
//        Long shopUserId = reviewUser.getShopUser().getShopUserId();
//        reviewUserRepository.delete(reviewUser);
//        reviewService.updateShopUserRating(shopUserId);
//    }


            // 리뷰 수정


            // 리뷰 삭제

    }

