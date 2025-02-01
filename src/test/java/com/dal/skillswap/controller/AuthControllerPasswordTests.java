package com.dal.skillswap.controller;

import com.dal.skillswap.enums.TokenValidationMessage;
import com.dal.skillswap.models.request.ChangePasswordRequest;
import com.dal.skillswap.models.request.ForgotPasswordRequest;
import com.dal.skillswap.models.response.ChangePasswordResponse;
import com.dal.skillswap.models.response.ForgotPasswordResponse;
import com.dal.skillswap.service.AuthService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.security.auth.login.AccountNotFoundException;
import java.util.UUID;

import static com.dal.skillswap.constants.MessageConstants.*;
import static com.dal.skillswap.utils.TestUtils.asJsonString;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerPasswordTests {

    @Mock
    private AuthService authService;

    @InjectMocks
    @Resource
    private AuthController authController;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testForgotPasswordExistingUser() throws Exception {
        ForgotPasswordResponse forgotPasswordResponse = new ForgotPasswordResponse(PASSWORD_RESET_URL_SENT);
        when(authService.forgotPassword(any(), any())).thenReturn(forgotPasswordResponse);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/user/auth/forgotPassword")
                        .content(asJsonString(getTestForgotPasswordRequest()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", notNullValue()))
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains(PASSWORD_RESET_URL_SENT));
    }

    @Test
    public void testForgotPasswordNonExistingUser() throws Exception {
        when(authService.forgotPassword(any(), any())).thenThrow(new BadCredentialsException(EMAIL_IS_NOT_REGISTERED));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/user/auth/forgotPassword")
                        .content(asJsonString(getTestForgotPasswordRequest()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message", notNullValue()))
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains(EMAIL_IS_NOT_REGISTERED));
    }

    @Test
    public void testForgotPasswordRandomException() throws Exception {
        when(authService.forgotPassword(any(), any())).thenThrow(new RuntimeException("Random Exception"));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/user/auth/forgotPassword")
                        .content(asJsonString(getTestForgotPasswordRequest()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message", notNullValue()))
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("Random Exception"));
    }


    @Test
    public void testValidateResetPasswordToken() throws Exception {
        when(authService.validatePasswordChangeToken(any())).thenReturn(TokenValidationMessage.SUCCESS);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/user/auth/validateToken")
                        .queryParam("token", String.valueOf(UUID.randomUUID()))
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains(TokenValidationMessage.SUCCESS.toString()));
    }

    @Test
    public void testChangePasswordExistingUserValidToken() throws Exception {
        when(authService.validatePasswordChangeToken(any())).thenReturn(TokenValidationMessage.SUCCESS);
        when(authService.changePassword(any())).thenReturn(new ChangePasswordResponse(PASSWORD_CHANGED_SUCCESSFULLY));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/user/auth/changePassword")
                        .content(asJsonString(getTestChangePasswordRequest()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", notNullValue()))
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains(PASSWORD_CHANGED_SUCCESSFULLY));
    }

    @Test
    public void testChangePasswordExistingUserInvalidToken() throws Exception {
        when(authService.validatePasswordChangeToken(any())).thenReturn(TokenValidationMessage.INVALID);
        when(authService.changePassword(any())).thenReturn(new ChangePasswordResponse(PASSWORD_CHANGED_SUCCESSFULLY));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/user/auth/changePassword")
                        .content(asJsonString(getTestChangePasswordRequest()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message", notNullValue()))
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains(TokenValidationMessage.INVALID.toString()));
    }

    @Test
    public void testChangePasswordNonExistingUserValidToken() throws Exception {
        when(authService.validatePasswordChangeToken(any())).thenReturn(TokenValidationMessage.SUCCESS);
        when(authService.changePassword(any())).thenThrow(new AccountNotFoundException(USER_NOT_FOUND));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/user/auth/changePassword")
                        .content(asJsonString(getTestChangePasswordRequest()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message", notNullValue()))
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains(USER_NOT_FOUND));
    }

    private ForgotPasswordRequest getTestForgotPasswordRequest() {
        return new ForgotPasswordRequest("test@test.com");
    }

    private ChangePasswordRequest getTestChangePasswordRequest() {
        return new ChangePasswordRequest(UUID.randomUUID().toString(), "test123");
    }

}
