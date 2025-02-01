package com.dal.skillswap.mapper;

import com.dal.skillswap.entities.Review;
import com.dal.skillswap.entities.User;
import com.dal.skillswap.enums.UserRole;
import com.dal.skillswap.models.request.ReviewRequest;
import com.dal.skillswap.models.response.ReviewResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ReviewMapperImpl.class})
class ReviewMapperTest {

    @Autowired
    private ReviewMapper reviewMapper;

    @Test
    public void testMapReviewEntityToRequest() {
        Review reviewEntity = new Review();
        reviewEntity.setId(1L);
        reviewEntity.setReviewer(getSampleUserEntity());
        reviewEntity.setReviewee(getUserEntityReviewee());

        ReviewRequest reviewRequest = reviewMapper.mapReviewEntityToRequest(reviewEntity);

        assertEquals(reviewEntity.getReviewer().getId(), reviewRequest.getReviewerId());
    }

    @Test
    public void testMapReviewRequestToEntity() {
        ReviewRequest reviewRequest = new ReviewRequest();
        reviewRequest.setReviewerId(2L);
        reviewRequest.setRevieweeId(3L);

        Review reviewEntity = reviewMapper.mapReviewRequestToEntity(reviewRequest);

        assertEquals(reviewRequest.getReviewerId(), reviewEntity.getReviewer().getId());
    }

    @Test
    public void testMapReviewEntityToResponse() {
        Review reviewEntity = new Review();
        reviewEntity.setId(1L);
        reviewEntity.setReviewer(getSampleUserEntity());
        reviewEntity.setReviewee(getUserEntityReviewee());

        ReviewResponse reviewResponse = reviewMapper.mapReviewEntityToResponse(reviewEntity);

        assertEquals(reviewEntity.getId(), reviewResponse.getId());
    }

    private User getSampleUserEntity() {
        return new User("test", "test", "test@test.com", "", UserRole.USER, "B3H1B9",
                "test123", 0.0, 0.0);
    }

    private User getUserEntityReviewee() {
        return new User("test2", "test2", "test2@test2.com", "", UserRole.USER, "B3H1B9",
                "test123", 0.0, 0.0);
    }
}
