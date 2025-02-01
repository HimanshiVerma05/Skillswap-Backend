package com.dal.skillswap.controller;

import com.dal.skillswap.enums.UserRole;
import com.dal.skillswap.mapper.PostMapperImpl;
import com.dal.skillswap.models.response.Post;
import com.dal.skillswap.models.response.Skill;
import com.dal.skillswap.models.response.User;
import com.dal.skillswap.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collections;

import static com.dal.skillswap.utils.TestUtils.asJsonString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PostController.class, PostMapperImpl.class})
@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles({"test"})
public class PostControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @BeforeEach
    void setup() {
        openMocks(this);
    }

    @Test
    void testAddPost() throws Exception {
        Post post = getPostResponseObject();
        when(postService.addPost(any())).thenReturn(post);

        MvcResult result = mockMvc.perform(post("/user/post/addPost")
                        .content(asJsonString(post))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains(post.getContent()));

    }

    @Test
    void testFetchPosts() throws Exception {
        Post post = getPostResponseObject();
        when(postService.fetchPosts(any(), any())).thenReturn(Collections.singletonList(post));

        URI targetUrl = UriComponentsBuilder.fromUriString("/user/post/getPosts")
                .build().encode().toUri();
        MvcResult result = mockMvc.perform(get(targetUrl)
                        .param("skillId", "1")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("testSkill"));

    }

    @Test
    void testDeletePost() throws Exception {

        MvcResult result = mockMvc.perform(delete("/user/post/deletePost")
                        .content(asJsonString(1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals("Post deleted successfully!", content);

    }

    Post getPostResponseObject() {
        Post post = new Post(1L, "content",
                new User(4L, "test", "test", "test@test.com", "1234567890",
                        UserRole.USER, "", true, "", 24, "",
                        "", "", "", null, null, null, 0.0, 0.0),
                new Skill(8L, "testSkill"),
                null, LocalDateTime.now(), null);
        return post;
    }

}
