package com.dal.skillswap.service.impl;

import com.dal.skillswap.entities.Post;
import com.dal.skillswap.mapper.PostMapper;
import com.dal.skillswap.repository.PostRepository;
import com.dal.skillswap.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostMapper postMapper;

    /**
     * Add a new post to the post repository
     * @param post
     * @return
     */
    @Override
    public com.dal.skillswap.models.response.Post addPost(Post post) {

        return postMapper.toPostModel(postRepository.save(post));
    }

    /**
     * Fetch all posts
     * @param skillId
     * @param userId
     * @return
     */
    @Override
    public List<com.dal.skillswap.models.response.Post> fetchPosts(Optional<Long> skillId, Optional<Long> userId) {

        if (skillId.isPresent() && userId.isPresent()) {
            List<Post> postsBySkillAndUser = postRepository.findBySkill_SkillIdAndUser_Id(skillId.get(), userId.get());
            return postsBySkillAndUser.stream().map(postMapper::toPostModel).collect(Collectors.toList());
        }
        if (skillId.isPresent()) {
            List<Post> postsBySkill = postRepository.findBySkill_SkillId(skillId.get());
            return postsBySkill.stream().map(postMapper::toPostModel).collect(Collectors.toList());
        }
        if (userId.isPresent()) {
            return postRepository.findByUser_Id(userId.get()).stream().map(postMapper::toPostModel).collect(Collectors.toList());
        }
        return postRepository.findAll().stream().map(postMapper::toPostModel).collect(Collectors.toList());
    }

    /**
     * Delete a post from the post repository
     * @param postId
     */
    @Override
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }
}
