package com.dal.skillswap.controller;

import com.dal.skillswap.entities.EmailValidation;
import com.dal.skillswap.entities.User;
import com.dal.skillswap.enums.UserRole;
import com.dal.skillswap.models.request.UserLoginRequest;
import com.dal.skillswap.models.request.UserSignupRequest;
import com.dal.skillswap.repository.EmailValidationRepository;
import com.dal.skillswap.repository.UserRepository;
import com.dal.skillswap.service.AuthService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.dal.skillswap.utils.TestUtils.asJsonString;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = true)
class AuthControllerTests {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailValidationRepository emailValidationRepository;

    @Autowired
    private MockMvc mockMvc;

    @Value("${frontend.url}")
    private String FRONTEND_URL;

    /**
     * Delete the test data in case it's already present in database
     */
    @BeforeEach
    public void deleteTestEntry() {
        if (userRepository.existsByEmail("test@test.com")) {
            userRepository.deleteByEmail("test@test.com");
        }
    }

    /**
     * This test checks whether a new user is able to signup
     *
     * @throws Exception
     */
    @Test
    public void testSignupNewUser() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/user/auth/signup")
                        .content(asJsonString(getTestSignupRequest()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", notNullValue()))
                .andExpect(jsonPath("$.token", notNullValue()))
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("test@test.com"));
    }

    /**
     * This tests checks that existing user should not be able to signup
     *
     * @throws Exception
     */
    @Test
    public void testSignupExistingUser() throws Exception {
        userRepository.save(getTestUser());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/user/auth/signup")
                        .content(asJsonString(getTestSignupRequest()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.httpStatus", notNullValue()))
                .andExpect(jsonPath("$.message", notNullValue()))
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("Email already exists"));
    }

    /**
     * This tests checks whether an existing user is able to login
     *
     * @throws Exception
     */
    @Test
    public void testLoginUserExists() throws Exception {
        userRepository.save(getTestUser());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/user/auth/login")
                        .content(asJsonString(getTestLoginRequest()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", notNullValue()))
                .andExpect(jsonPath("$.token", notNullValue()))
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("test@test.com"));
    }

    /**
     * This tests checks that non-existing user shouldn't be able to login
     *
     * @throws Exception
     */
    @Test
    public void testLoginUserDoesntExist() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/user/auth/login")
                        .content(asJsonString(getTestLoginRequest()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.httpStatus", notNullValue()))
                .andExpect(jsonPath("$.message", notNullValue()))
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("Invalid username or password"));
    }

    @Test
    void testEmailValidationSuccess() throws Exception {
        String verificationCode = "valid_verification_code";

        User user = userRepository.save(getTestUser());
        emailValidationRepository.save(new EmailValidation(user, "valid_verification_code"));


        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/user/auth/verify/" + verificationCode))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(FRONTEND_URL+"/email_verification_success"))
                .andReturn();

        assertEquals(result.getResponse().getStatus(), HttpStatus.FOUND.value());
    }

    @Test
    void testEmailValidationFailure() throws Exception {
        String verificationCode = "invalid_verification_code";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/user/auth/verify/" + verificationCode))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("Email verification failed."))
                .andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Email verification failed."));
    }

    private EmailValidation getTestEmailValidation() {
        return new EmailValidation(getTestUser(), "valid_verification_code");
    }

    private User getTestUser() {
        return new User("test", "test", "test@test.com", "123-456-7890", UserRole.USER,
                "1AB23H", passwordEncoder.encode("test123"), 0.0, 0.0);
    }

    private UserSignupRequest getTestSignupRequest() {
        return new UserSignupRequest("test", "test", "123-456-7890",
                "test@test.com", "B3H1B9", passwordEncoder.encode("test123"));
    }

    private UserLoginRequest getTestLoginRequest() {
        return new UserLoginRequest("test@test.com", "test123");
    }

}
