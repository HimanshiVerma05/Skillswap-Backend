package com.dal.skillswap.models.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {
    private Long reviewerId;
    private Long revieweeId;
    private int starRating;
    private String feedback;
    private Date createdAt;
    private String reviewImageName;
}
