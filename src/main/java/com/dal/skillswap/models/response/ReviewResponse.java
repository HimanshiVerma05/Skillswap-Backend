package com.dal.skillswap.models.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private Long reviewerId;
    private String reviewerName;
    private int starRating;
    private String feedback;
    private Date createdAt;
    private String reviewImageName;
}

