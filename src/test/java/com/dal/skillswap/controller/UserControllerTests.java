package com.dal.skillswap.controller;

import com.dal.skillswap.enums.UserRole;
import com.dal.skillswap.mapper.SkillMapperImpl;
import com.dal.skillswap.mapper.UserMapperImpl;
import com.dal.skillswap.service.FileStoreService;
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
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.dal.skillswap.constants.PathConstants.PROFILE_IMAGES;
import static com.dal.skillswap.utils.TestUtils.asJsonString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserController.class, FileStoreServiceImpl.class,
        SkillMapperImpl.class, UserMapperImpl.class})
@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles({"test"})
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private FileStoreService fileStoreService;

    @BeforeEach
    void setup() {
        openMocks(this);
    }

    @Test
    void testHelloWorld() throws Exception {
        MvcResult result = mockMvc.perform(get("/user/test"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.equals("Hello World"));
    }

    @Test
    void testGetSkills() throws Exception {
        MvcResult result = mockMvc.perform(get("/user/getSkills"))
                .andExpect(status().isOk())
                .andReturn();

        assertNotNull(result);
    }

    @Test
    void testGetUserSkills() throws Exception {
        MvcResult result = mockMvc.perform(get("/user/getUserSkills"))
                .andExpect(status().isOk())
                .andReturn();

        assertNotNull(result);
    }

    @Test
    void testAddSkills() throws Exception {
        List<String> skills = new ArrayList<>();
        skills.add("test_skill1");
        skills.add("test_skill2");

        MvcResult result = mockMvc.perform(post("/user/addSkills")
                        .content(asJsonString(skills))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        assertNotNull(result);
    }

    @Test
    void testAddUserSkills() throws Exception {
        List<String> skills = new ArrayList<>();
        skills.add("testSkill1");
        skills.add("testSkills2");

        MvcResult result = mockMvc.perform(post("/user/addUserSkills")
                        .content(asJsonString(skills))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        assertNotNull(result);
    }

    @Test
    void testGetCoordinates() throws Exception {
        MvcResult result = mockMvc.perform(get("/user/getCoordinates"))
                .andExpect(status().isOk())
                .andReturn();

        assertNotNull(result);
    }

    @Test
    @WithMockUser
    void testGetNearbyUsers() throws Exception {
        MvcResult result = mockMvc.perform(get("/user/getUsersByLocationAndSkillId")
                        .param("range", "25")
                        .param("skillId", "2"))
                .andExpect(status().isOk())
                .andReturn();

        assertNotNull(result);
    }

    @Test
    void getUserDetailsWithEmailParamProvided() throws Exception {

        String email = "test@test.com";

        when(userService.getUserDetails(any())).thenReturn(getSampleResponseUserEntity());

        ObjectMapper objectMapper = new ObjectMapper();
        String expectedJsonResponse = objectMapper.writeValueAsString(getSampleResponseUserEntity());

        MvcResult result = mockMvc.perform(get("/user/details")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJsonResponse))
                .andReturn();

        assertNotNull(result);
    }

    @Test
    void getUserDetailsWithNoEmailParam() throws Exception {

        when(userService.getLoggedInUserEmail()).thenReturn("test@example.com");
        when(userService.getUserDetails("test@example.com")).thenReturn(getSampleResponseUserEntity());

        ObjectMapper objectMapper = new ObjectMapper();
        String expectedJsonResponse = objectMapper.writeValueAsString(getSampleResponseUserEntity());

        MvcResult result = mockMvc.perform(get("/user/details"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJsonResponse))
                .andReturn();

        assertNotNull(result);
    }

    @Test
    void updateUserDetailsSuccess() throws Exception {
        com.dal.skillswap.models.request.User user = getSampleRequestUserEntity();
        when(userService.updateUserDetails(user)).thenReturn(true);

        MvcResult result = mockMvc.perform(post("/user/details/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(true)))
                .andReturn();

        assertNotNull(result);

    }

    @Test
    void updateUserDetailsInternalServerError() throws Exception {
        com.dal.skillswap.models.request.User user = getSampleRequestUserEntity();
        when(userService.updateUserDetails(user)).thenThrow(new RuntimeException("Failed to update user details"));

        MvcResult result = mockMvc.perform(post("/user/details/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(user)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Failed to update user details"))
                .andReturn();

        assertNotNull(result);

    }

    @Test
    void handleFileUploadSuccess() throws Exception {
        byte[] fileContent = "test file content".getBytes();
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", fileContent);
        String storedFilePath = "/profile_images/test.jpg";

        when(fileStoreService.store(any(), anyString())).thenReturn(storedFilePath);

        MvcResult result = mockMvc.perform(multipart("/user/profilePicture/save")
                        .file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(content().string(storedFilePath))
                .andReturn();

        assertNotNull(result);
    }

    @Test
    void handleFileUploadInternalServerError() throws Exception {
        byte[] fileContent = "test file content".getBytes();
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", fileContent);

        when(fileStoreService.store(any(), anyString())).thenThrow(new RuntimeException("Failed to store file"));

        MvcResult result = mockMvc.perform(multipart("/user/profilePicture/save")
                        .file(multipartFile))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Failed to store file"))
                .andReturn();
        assertNotNull(result);
    }

    @Test
    void getFileSuccess() throws Exception {
        String filename = "test.jpg";
        byte[] fileContent = "test file content".getBytes();
        Resource resource = new InputStreamResource(new ByteArrayInputStream(fileContent));

        when(fileStoreService.load(filename, PROFILE_IMAGES)).thenReturn(resource);

        MockHttpServletResponse response = mockMvc.perform(get("/user/profilePicture/{filename}", filename))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        assertArrayEquals(fileContent, response.getContentAsByteArray());
    }

    @Test
    void getFileNotFound() throws Exception {
        String filename = "nonexistent.jpg";

        when(fileStoreService.load(filename, PROFILE_IMAGES)).thenReturn(null);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/profilePicture/{filename}", filename))
                .andExpect(status().isNotFound())
                .andReturn();

        assertNotNull(result);
    }

    @Test
    void deleteUserSkillsSuccess() throws Exception {
        String loggedInUser = "test@test.com";
        when(userService.getLoggedInUserEmail()).thenReturn(loggedInUser);

        Long deletedCount = 3L;
        when(userService.deleteUserSkills(loggedInUser, Arrays.asList("Java", "Python", "SQL"))).thenReturn(deletedCount);

        MvcResult result = mockMvc.perform(delete("/user/deleteUserSkill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"Java\", \"Python\", \"SQL\"]"))
                .andExpect(status().isOk())
                .andExpect(content().string("User skills deleted successfully"))
                .andReturn();

        assertNotNull(result);
    }

    @Test
    void deleteUserSkillsNotFound() throws Exception {
        String loggedInUser = "test@example.com";
        when(userService.getLoggedInUserEmail()).thenReturn(loggedInUser);

        Long deletedCount = 0L;
        when(userService.deleteUserSkills(loggedInUser, Arrays.asList("Java", "Python", "SQL"))).thenReturn(deletedCount);

        MvcResult result = mockMvc.perform(delete("/user/deleteUserSkill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"Java\", \"Python\", \"SQL\"]"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No user skills found with the provided skill names"))
                .andReturn();

        assertNotNull(result);
    }

    @Test
    void deleteSkillSuccess() throws Exception {
        Long deletedCount = 3L;
        when(userService.deleteSkills(Arrays.asList("Java", "Python", "SQL"))).thenReturn(deletedCount);

        MvcResult result = mockMvc.perform(delete("/user/deleteSkill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"Java\", \"Python\", \"SQL\"]"))
                .andExpect(status().isOk())
                .andExpect(content().string("User skills deleted successfully"))
                .andReturn();

        assertNotNull(result);
    }

    @Test
    void deleteSkillNotFound() throws Exception {
        Long deletedCount = 0L;
        when(userService.deleteSkills(Arrays.asList("Java", "Python", "SQL"))).thenReturn(deletedCount);

        MvcResult result = mockMvc.perform(delete("/user/deleteSkill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"Java\", \"Python\", \"SQL\"]"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No user skills found with the provided skill names"))
                .andReturn();

        assertNotNull(result);
    }

    @Test
    void getUserNameSuccess() throws Exception {
        String loggedInUser = "test@example.com";
        when(userService.getLoggedInUserEmail()).thenReturn(loggedInUser);

        MvcResult result = mockMvc.perform(get("/user/userName"))
                .andExpect(status().isOk())
                .andExpect(content().string(loggedInUser))
                .andReturn();

        assertNotNull(result);
    }

    private com.dal.skillswap.models.response.User getSampleResponseUserEntity() {
        return new com.dal.skillswap.models.response.User(1L, "test", "test", "test@test.com", "1234567890",
                UserRole.USER, "", true, "testFile", 24, "", "",
                "", "", null, null, null, 0.0, 0.0);
    }

    private com.dal.skillswap.models.request.User getSampleRequestUserEntity() {
        return new com.dal.skillswap.models.request.User("test@test.com", "test123");
    }

}
