package com.dal.skillswap.service;

import com.dal.skillswap.entities.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {

    com.dal.skillswap.models.response.Post addPost(Post post);

    List<com.dal.skillswap.models.response.Post> fetchPosts(Optional<Long> skillId, Optional<Long> userId);

    void deletePost(Long postId);
}
