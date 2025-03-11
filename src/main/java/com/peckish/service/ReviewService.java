package com.peckish.service;

import com.peckish.dto.ReviewFormDTO;
import jakarta.transaction.Transactional;

@Transactional
public interface ReviewService {

    Long add(Long shopId, String infoType, ReviewFormDTO reviewFormDTO);


}
