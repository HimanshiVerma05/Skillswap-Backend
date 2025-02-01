package com.dal.skillswap.controller;

import com.dal.skillswap.mapper.SkillMapperImpl;
import com.dal.skillswap.mapper.UserMapperImpl;
import com.dal.skillswap.models.response.ReviewResponse;
import com.dal.skillswap.service.FileStoreService;
import com.dal.skillswap.service.ReviewService;
import com.dal.skillswap.service.UserService;
import com.dal.skillswap.service.impl.FileStoreServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ReviewController.class, UserController.class, FileStoreServiceImpl.class,
        SkillMapperImpl.class, UserMapperImpl.class})
@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles({"test"})
public class ReviewControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;
    @MockBean
    private UserService userService;

    @MockBean
    private FileStoreService fileStoreService;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        openMocks(this);
    }

    @Test
    void testCreateReview() throws Exception {

        String dummyFileName = "test.jpg";
        when(fileStoreService.store(any(MultipartFile.class), anyString())).thenReturn(dummyFileName);

        String reviewRequestJson = "{\"reviewerId\":1,\"revieweeId\":2,\"starRating\":5,\"feedback\":\"Great service\",\"createdAt\":\"2022-03-28\"}";

        MockMultipartFile mockFile = new MockMultipartFile(
                "reviewImage",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[]{});

        MockMultipartFile reviewRequestJsonPart = new MockMultipartFile(
                "reviewRequest",
                "reviewRequest.json",
                "application/json",
                reviewRequestJson.getBytes());


        MvcResult result = mockMvc.perform(multipart("/review/create")
                        .file(mockFile)
                        .file(reviewRequestJsonPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string("Review created successfully"))
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("created"), "Review creation successfully");
    }

    @Test
    void testGetReviewsByReviewee() throws Exception {
        when(reviewService.getReviewsByReviewee(any())).thenReturn(Collections.emptyList());

        MvcResult result = mockMvc.perform(get("/review/{revieweeId}", 1L))
                .andExpect(status().isOk())
                .andReturn();

        List<ReviewResponse> reviews = objectMapper.readValue(result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, ReviewResponse.class));

        assertTrue(reviews.isEmpty(), "Expected no reviews for this reviewee");

    }

    @Test
    void testGetReviewImage() throws Exception {
        Resource mockResource = mock(Resource.class);
        when(fileStoreService.load(anyString(), anyString())).thenReturn(mockResource);

        MvcResult result = mockMvc.perform(get("/review/reviewImage/test.jpg"))
                .andExpect(status().isOk())
                .andReturn();

        assertTrue(result.getResponse().getContentLength() == 0, "Image content is empty");
    }

    @Test
    void testDeleteReview() throws Exception {
        when(reviewService.deleteReview(anyLong())).thenReturn(true);

        MvcResult result = mockMvc.perform(delete("/review/delete/{reviewId}", 1L))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        assertTrue(responseContent.contains("Review deleted successfully"), "Expected message not found in response");
    }
}

