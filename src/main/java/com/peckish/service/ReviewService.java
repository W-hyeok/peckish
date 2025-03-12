package com.peckish.service;

import com.peckish.dto.ReviewFormDTO;
import com.peckish.dto.ReviewRespDTO;
import jakarta.transaction.Transactional;

import java.util.List;

@Transactional
public interface ReviewService {

    // 리뷰 등록
    Long add(Long shopId, String infoType, ReviewFormDTO reviewFormDTO);
    // User - 리뷰 목록 조회
    List<ReviewRespDTO> getReviewUser(Long shopId);
    // Owner - 리뷰 목록 조회
    List<ReviewRespDTO> getReviewOwner(Long shopId);
    // User - 리뷰 별점 조회
    Double updateShopUserRating(Long shopId);
    // Owner - 리뷰 별점 조회
    Double findOwnerRatingAvg(Long shopId);
    // 리뷰 삭제시에도 별점평균 처리
    public void deleteReview(Long reviewId);
    // 리뷰 수정



    // 리뷰 삭제

}
