package com.dal.skillswap.service.impl;

import com.dal.skillswap.entities.Review;
import com.dal.skillswap.mapper.ReviewMapper;
import com.dal.skillswap.models.request.ReviewRequest;
import com.dal.skillswap.models.response.ReviewResponse;
import com.dal.skillswap.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReviewServiceImplTests {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateReview() {
        ReviewRequest reviewRequest = new ReviewRequest();
        Review reviewEntity = new Review();
        when(reviewMapper.mapReviewRequestToEntity(reviewRequest)).thenReturn(reviewEntity);

        reviewService.createReview(reviewRequest);

        verify(reviewRepository, times(1)).save(reviewEntity);
    }

    @Test
    public void testGetReviewsByReviewee() {
        Long revieweeId = 123L;
        List<Review> reviewEntities = new ArrayList<>();
        reviewEntities.add(new Review());
        when(reviewRepository.findByRevieweeId(revieweeId)).thenReturn(reviewEntities);

        List<ReviewResponse> expectedResponses = new ArrayList<>();
        expectedResponses.add(new ReviewResponse());
        when(reviewMapper.mapReviewEntityToResponse(any(Review.class))).thenReturn(new ReviewResponse());

        List<ReviewResponse> actualResponses = reviewService.getReviewsByReviewee(revieweeId);

        assertEquals(expectedResponses.size(), actualResponses.size());
        verify(reviewMapper, times(reviewEntities.size())).mapReviewEntityToResponse(any(Review.class));
    }

    @Test
    void testDeleteReview() {
        Review reviewEntity = new Review();
        reviewEntity.setId(1L);
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(reviewEntity));
        boolean deleted = reviewService.deleteReview(1L);

        assertTrue(deleted);
        verify(reviewRepository, times(1)).delete(reviewEntity);
    }

    @Test
    void testDeleteReviewNotFound() {
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        boolean deleted = reviewService.deleteReview(1L);
        assertFalse(deleted);
        verify(reviewRepository, never()).delete(any());
    }
}

