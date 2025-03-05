package com.peckish.service;

import com.peckish.dto.ReviewFormDTO;
import com.peckish.repository.ReviewOwnerRepository;
import com.peckish.repository.ReviewUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReviewServierImpl implements ReviewService {

    private final ReviewUserRepository reviewUserRepository;
    private final ReviewOwnerRepository reviewOwnerRepository;


    @Override
    public Long save(Long shopId, Long ShopDetailId, String infoType, ReviewFormDTO reviewFormDTO) {

        log.info("리뷰 등록 - reviewFormDTO : {}", reviewFormDTO);
        log.info("hello");
        // User일 때
        if(infoType.equals("USER")){
        

        }
        // Owner일 때
        else if(infoType.equals("OWNER")){

        }

        return 1L;
    }
}
