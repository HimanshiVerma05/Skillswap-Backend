package com.dal.skillswap.service;

import com.dal.skillswap.models.request.ReviewRequest;
import com.dal.skillswap.models.response.ReviewResponse;

import java.util.List;

public interface ReviewService {
    void createReview(ReviewRequest reviewRequest);

    List<ReviewResponse> getReviewsByReviewee(Long revieweeId);

    public Boolean deleteReview(Long reviewId);

}


