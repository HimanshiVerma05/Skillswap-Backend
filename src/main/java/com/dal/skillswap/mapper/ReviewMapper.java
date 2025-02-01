package com.dal.skillswap.mapper;

import com.dal.skillswap.entities.Review;
import com.dal.skillswap.entities.User;
import com.dal.skillswap.models.request.ReviewRequest;
import com.dal.skillswap.models.response.ReviewResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(target = "reviewerId", source = "review.reviewer.id")
    @Mapping(target = "revieweeId", source = "review.reviewee.id")
    ReviewRequest mapReviewEntityToRequest(Review review);

    @Mapping(target = "reviewer",source = "reviewRequest.reviewerId", qualifiedByName = "getUserFromId")
    @Mapping(target = "reviewee",source = "reviewRequest.revieweeId", qualifiedByName = "getUserFromId")
    Review mapReviewRequestToEntity(ReviewRequest reviewRequest);

    @Mapping(target = "reviewerId", source = "review.reviewer.id")
    @Mapping(target = "reviewerName", expression = "java(review.getReviewer().getFirstName() + \" \" + review.getReviewer().getLastName())")
    ReviewResponse mapReviewEntityToResponse(Review review);

    @Named("getUserFromId")
    default User getUserFromId(Long reviewerId) {
        User reviewer = new User();
        reviewer.setId(reviewerId);
        return reviewer;
    }
}