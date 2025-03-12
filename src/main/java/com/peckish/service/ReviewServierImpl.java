package com.peckish.service;

import com.peckish.domain.*;
import com.peckish.dto.ReviewFormDTO;
import com.peckish.dto.ReviewRespDTO;
import com.peckish.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServierImpl implements ReviewService {

    private final ShopService shopservice;
    private final ReviewUserRepository reviewUserRepository;
    private final ReviewOwnerRepository reviewOwnerRepository;
    private final ShopUserRepository shopUserRepository;
    private final MemberRepository memberRepository;
    private final ShopRepository shopRepository;
    private final ShopOwnerRepository shopOwnerRepository;

    // 리뷰 등록
    @Override
    public Long add(Long shopId, String infoType, ReviewFormDTO reviewFormDTO) {
        log.info("리뷰 등록 - ReviewFormDTO : {}", reviewFormDTO);
        Long savedId = null; // reviewUser와 reviewOwner에 저장된 pk
        // 작성자 정보 저장
        Member member = memberRepository.findById(reviewFormDTO.getEmail()).orElseThrow();

        if(reviewFormDTO.getInfoType().equals("USER")){
            // infoType이 USER면 USER쪽에 저장
            // 입력받은 reviewFormDTO를 DB에 저장하기 위해서 Entity로 변경
            ReviewUser reviewUserEntity = reviewFormDTO.toReviewUserEntity();
            //ShopUser 호출해서 어느 상점번호에 저장할지 결정

            // repository에서 조회해서 객체로 저장
            ShopUser findShopUser = shopUserRepository.findById(reviewFormDTO.getShopDetailId()).orElseThrow();
            Shop findShop = shopRepository.findById(shopId).orElseThrow();

            // 이메일 정보 넣어주기
            reviewUserEntity.setMember(member);
            // 연관관계는 객체로 넣어줌
            reviewUserEntity.setShopUser(findShopUser); // shopUser 저장
            reviewUserEntity.setShop(findShop); // shop 저장
            reviewUserEntity.setRegDate(LocalDateTime.now());
            reviewUserEntity.setUpdateDate(LocalDateTime.now());

            //DB에 Entity 저장
            ReviewUser save = reviewUserRepository.save(reviewUserEntity);
            savedId = save.getReviewUserId();

        } else if(reviewFormDTO.getInfoType().equals("OWNER")){
            // dto -> Entity
            ReviewOwner reviewOwnerEntity = reviewFormDTO.toReviewOwnerEntity();

            ShopOwner findShopOwner = shopOwnerRepository.findById(reviewFormDTO.getShopDetailId()).orElseThrow();
            Shop findShop = shopRepository.findById(shopId).orElseThrow();

            reviewOwnerEntity.setMember(member);
            reviewOwnerEntity.setShopOwner(findShopOwner);
            reviewOwnerEntity.setShop(findShop);
            reviewOwnerEntity.setRegDate(LocalDateTime.now());
            reviewOwnerEntity.setUpdateDate(LocalDateTime.now());

            ReviewOwner save = reviewOwnerRepository.save(reviewOwnerEntity);
            savedId = save.getReviewOwnerId();
        }
        return savedId;
    }
    // User - 리뷰조회
    @Override
    public List<ReviewRespDTO> getReviewUser(Long shopId) {
        log.info("getReviewUser - shopId : {}", shopId);

       ShopUser shopUser = shopUserRepository.selectReviewUserByShopId(shopId);

        if(!shopUser.getReviewUser().isEmpty()){
            List<ReviewRespDTO> reviewUserList = shopUser.getReviewUser().stream()
                    .map(reviewEntity -> new ReviewRespDTO(reviewEntity))
                    .collect(Collectors.toList());
            return reviewUserList;

        }else {
            return null;
        }
    }

    // Owner - 리뷰조회
    @Override
    public List<ReviewRespDTO> getReviewOwner(Long shopId) {
        log.info("getReviewOwner - shopId : {}", shopId);

        ShopOwner shopOwner = shopOwnerRepository.selectReviewOwnerByShopId(shopId);

        if(!shopOwner.getReviewOwner().isEmpty()){
            List<ReviewRespDTO> reviewOwnerList = shopOwner.getReviewOwner().stream()
                    .map(reviewEntity -> new ReviewRespDTO(reviewEntity))
                    .collect(Collectors.toList());
            return reviewOwnerList;
        } else{
            return null;
        }
    }

    @Override
    public Double updateShopUserRating(Long shopId) {
        // User - Review List 가져와 rating 평균 구하기
        ShopUser shopUser = shopUserRepository.selectReviewUserByShopId(shopId);

        double avg = shopUser.getReviewUser().stream()
                .mapToDouble(ReviewUser::getRating)
                .average()
                .orElse(0.0);
        shopUser.setRatingAvg(avg);
        shopUserRepository.save(shopUser);
        return avg;
    }

    @Override
    public Double findOwnerRatingAvg(Long shopId) {
        // Owner - Review List 가져와 rating 평균 구하기
        ShopOwner shopOwner = shopOwnerRepository.selectReviewOwnerByShopId(shopId);

        double avg = shopOwner.getReviewOwner().stream()
                .mapToDouble(ReviewOwner::getRating)
                .average()
                .orElse(0.0);
        shopOwner.setRatingAvg(avg);
        shopOwnerRepository.save(shopOwner);
        return avg;
    }

    @Override
    public void deleteReview(Long reviewId) {




    }


    // 리뷰 수정


    // 리뷰 삭제




}
