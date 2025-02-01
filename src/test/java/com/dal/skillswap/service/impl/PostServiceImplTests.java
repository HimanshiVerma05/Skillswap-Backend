package com.dal.skillswap.service.impl;

import com.dal.skillswap.entities.Skill;
import com.dal.skillswap.enums.UserRole;
import com.dal.skillswap.mapper.PostMapper;
import com.dal.skillswap.models.response.Post;
import com.dal.skillswap.models.response.User;
import com.dal.skillswap.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class PostServiceImplTests {

    @InjectMocks
    private PostServiceImpl postService;
    @Mock
    private PostRepository postRepository;
    @Spy
    private PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    //getting all posts
    @Test
    void testFetchAllPosts() {
        when(postRepository.findAll()).thenReturn(Arrays.asList(getPostEntityObject()));
        List<Post> postsResponse = postService.fetchPosts(Optional.empty(), Optional.empty());
        assertEquals(postsResponse.get(0).getContent(), "content");
    }

    @Test
    void testFetchPostsForUserId() {
        when(postRepository.findByUser_Id(any())).thenReturn(Arrays.asList(getPostEntityObject()));
        List<Post> postsResponse = postService.fetchPosts(Optional.empty(), Optional.of(1L));
        assertEquals(postsResponse.get(0).getContent(), "content");
    }

    @Test
    void testFetchPostsForSkillId() {
        when(postRepository.findBySkill_SkillId(any())).thenReturn(Arrays.asList(getPostEntityObject()));
        List<Post> postsResponse = postService.fetchPosts(Optional.of(1L), Optional.empty());
        assertEquals(postsResponse.get(0).getContent(), "content");
    }

    @Test
    void testFetchPostsForSkillIdAndUserId() {
        when(postRepository.findBySkill_SkillIdAndUser_Id(any(), any())).thenReturn(Arrays.asList(getPostEntityObject()));
        List<Post> postsResponse = postService.fetchPosts(Optional.of(1L), Optional.of(1L));
        assertEquals(postsResponse.get(0).getContent(), "content");
    }


    @Test
    void testAddPost() {
        when(postRepository.save(any())).thenReturn(getPostEntityObject());
        Post addPostResponse = postService.addPost(getPostEntityObject());
        assertEquals(addPostResponse.getContent(), "content");
    }

    @Test
    void testDeletePost() {
        postService.deletePost(getPostEntityObject().getPostId());
        ArgumentCaptor<Long> postCaptor = ArgumentCaptor.forClass(Long.class);
        verify(postRepository, times(1)).deleteById(postCaptor.capture());
        assertEquals(1, postCaptor.getValue());
    }

    Post getPostResponseObject() {
        Post post = new Post(1L, "content",
                new User(4L, null, null, "test@test.com", "1234567890", UserRole.USER,
                        "", true, "", 24, "", "",
                        "", "", null, null, null, 0.0, 0.0),
                new com.dal.skillswap.models.response.Skill(1L, "testSkill"),
                null, LocalDateTime.now(), null);
        return post;
    }

    private com.dal.skillswap.entities.Post getPostEntityObject() {
        com.dal.skillswap.entities.Post post = new com.dal.skillswap.entities.Post(1L,
                new com.dal.skillswap.entities.User("test", "test", null, "test@test.com", UserRole.USER, null, null, 0.0, 0.0),
                new Skill("testSkill"),
                "content", null, LocalDateTime.now(), null);
        return post;
    }
}
