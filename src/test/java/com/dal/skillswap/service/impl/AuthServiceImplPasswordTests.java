package com.dal.skillswap.service.impl;

import com.dal.skillswap.entities.PasswordResetToken;
import com.dal.skillswap.entities.User;
import com.dal.skillswap.enums.TokenValidationMessage;
import com.dal.skillswap.models.request.ChangePasswordRequest;
import com.dal.skillswap.models.request.ForgotPasswordRequest;
import com.dal.skillswap.models.response.ChangePasswordResponse;
import com.dal.skillswap.models.response.ForgotPasswordResponse;
import com.dal.skillswap.repository.PasswordTokenRepository;
import com.dal.skillswap.repository.UserRepository;
import com.dal.skillswap.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.security.auth.login.AccountNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.dal.skillswap.constants.MessageConstants.PASSWORD_CHANGED_SUCCESSFULLY;
import static com.dal.skillswap.constants.MessageConstants.PASSWORD_RESET_URL_SENT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AuthServiceImplPasswordTests {
    private static final int EXPIRATION_DAYS = 1;
    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordTokenRepository passwordTokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    public void testForgotPasswordExistingUser() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));
        ForgotPasswordResponse forgotPasswordResponse = authService.forgotPassword(getMockHttpServletRequest(), getMockForgotPasswordRequest());
        assertTrue(forgotPasswordResponse.getMessage().contains(PASSWORD_RESET_URL_SENT));
    }

    @Test
    public void testForgotPasswordNonExistingUser() throws Exception {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        assertThrows(BadCredentialsException.class, () -> authService.forgotPassword(getMockHttpServletRequest(), getMockForgotPasswordRequest()));
    }

    @Test
    public void testChangePasswordExistingUser() throws Exception {
        when(passwordTokenRepository.findUserByToken(anyString())).thenReturn(Optional.of(new User()));
        ChangePasswordResponse changePasswordResponse = authService.changePassword(getMockChangePasswordRequest());
        assertTrue(changePasswordResponse.getMessage().contains(PASSWORD_CHANGED_SUCCESSFULLY));
    }

    @Test
    public void testChangePasswordNonExistingUser() throws Exception {
        when(passwordTokenRepository.findUserByToken(anyString())).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> authService.changePassword(getMockChangePasswordRequest()));
    }

    /**
     * Test to validate a successful token
     */
    @Test
    public void testValidateSuccessPasswordToken() {
        when(passwordTokenRepository.findByToken(anyString())).thenReturn(Optional.of(getMockPasswordResetToken()));
        TokenValidationMessage tokenValidationMessage = authService.validatePasswordChangeToken(UUID.randomUUID().toString());

        assertEquals(tokenValidationMessage, TokenValidationMessage.SUCCESS);
    }

    /**
     * Test to check invalid token
     */
    @Test
    public void testValidateInvalidPasswordToken() {
        when(passwordTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());
        TokenValidationMessage tokenValidationMessage = authService.validatePasswordChangeToken(UUID.randomUUID().toString());
        assertEquals(tokenValidationMessage, TokenValidationMessage.INVALID);
    }

    /**
     * Test to check expired token
     */
    @Test
    public void testValidateExpiredPasswordToken() {
        PasswordResetToken passwordResetToken = getMockPasswordResetToken();
        passwordResetToken.setTokenExpiryDate(LocalDateTime.now().minusDays(EXPIRATION_DAYS));
        when(passwordTokenRepository.findByToken(anyString())).thenReturn(Optional.of(passwordResetToken));
        TokenValidationMessage tokenValidationMessage = authService.validatePasswordChangeToken(UUID.randomUUID().toString());

        assertEquals(tokenValidationMessage, TokenValidationMessage.EXPIRED);
    }

    private ForgotPasswordRequest getMockForgotPasswordRequest() {
        return new ForgotPasswordRequest("test@test.com");
    }

    private ChangePasswordRequest getMockChangePasswordRequest() {
        return new ChangePasswordRequest(UUID.randomUUID().toString(), "test890");
    }

    private HttpServletRequest getMockHttpServletRequest() {
        return Mockito.mock(HttpServletRequest.class);
    }

    private PasswordResetToken getMockPasswordResetToken() {
        return new PasswordResetToken(UUID.randomUUID().toString(), new User());
    }

}