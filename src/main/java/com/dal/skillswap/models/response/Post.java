package com.dal.skillswap.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    private Long postId;

    private String content;
    private User user;
    private Skill skill;
    private String image;
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

