package com.dal.skillswap.service.impl;

import com.dal.skillswap.entities.Review;
import com.dal.skillswap.mapper.ReviewMapper;
import com.dal.skillswap.models.request.ReviewRequest;
import com.dal.skillswap.models.response.ReviewResponse;
import com.dal.skillswap.repository.ReviewRepository;
import com.dal.skillswap.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewMapper reviewMapper;

    /**
     * Create a new review
     * @param reviewRequest
     */
    @Override
    public void createReview(ReviewRequest reviewRequest) {
        Review reviewEntity = reviewMapper.mapReviewRequestToEntity(reviewRequest);
        reviewRepository.save(reviewEntity);
    }

    /**
     * Get review by id
     * @param revieweeId
     * @return
     */
    @Override
    public List<ReviewResponse> getReviewsByReviewee(Long revieweeId) {
        List<Review> reviewEntities = reviewRepository.findByRevieweeId(revieweeId);
        return reviewEntities.stream()
                .map(reviewMapper::mapReviewEntityToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Delete a review from the review repository
     * @param reviewId
     * @return
     */
    @Override
    public Boolean deleteReview(Long reviewId) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        if (optionalReview.isPresent()) {
            reviewRepository.delete(optionalReview.get());
            return true;
        }
        return false;
    }

}

