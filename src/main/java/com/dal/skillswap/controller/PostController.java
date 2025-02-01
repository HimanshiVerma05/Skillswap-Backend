package com.dal.skillswap.controller;

import com.dal.skillswap.mapper.PostMapper;
import com.dal.skillswap.models.response.ErrorResponse;
import com.dal.skillswap.models.response.Post;
import com.dal.skillswap.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user/post")
public class PostController {

    @Autowired
    private PostService postService;
    @Autowired
    private PostMapper postMapper;

    @GetMapping("/getPosts")
    public ResponseEntity<List<Post>> getPosts(@RequestParam Optional<Long> skillId, @RequestParam Optional<Long> userId) {
        List<Post> posts = postService.fetchPosts(skillId, userId);
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/addPost")
    @ResponseBody
    public ResponseEntity<Post> addPost(@RequestBody Post post) {
        com.dal.skillswap.entities.Post postEntity = postMapper.toPostEntity(post);
        Post addedPost = postService.addPost(postEntity);
        return ResponseEntity.ok(addedPost);
    }

    @DeleteMapping("/deletePost")
    @ResponseBody
    public ResponseEntity deletePost(@RequestBody Long postId) {
        try {
            postService.deletePost(postId);
        } catch (RuntimeException e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
        return ResponseEntity.ok("Post deleted successfully!");
    }

}
