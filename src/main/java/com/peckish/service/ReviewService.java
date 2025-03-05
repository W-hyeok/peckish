package com.peckish.service;

import com.peckish.dto.ReviewFormDTO;


public interface ReviewService {

    Long save (Long shopId, Long ShopDetailId, String infoType, ReviewFormDTO reviewDTO);


}
