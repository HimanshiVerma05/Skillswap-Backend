package com.dal.skillswap.service.impl;

import com.dal.skillswap.entities.EmailValidation;
import com.dal.skillswap.entities.User;
import com.dal.skillswap.repository.EmailValidationRepository;
import com.dal.skillswap.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailValidationServiceTests {

    @Mock
    private EmailValidationRepository emailValidationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testVerifyByEmail_Success() {
        String code = "valid_code";
        EmailValidation emailValidation = new EmailValidation();
        emailValidation.setCode(code);
        User userToVerify = new User();
        userToVerify.setIsVerified(false);
        emailValidation.setUser(userToVerify);

        List<EmailValidation> emailValidationList = new ArrayList<>();
        emailValidationList.add(emailValidation);

        when(emailValidationRepository.getEmailValidationsByCode(code)).thenReturn(emailValidationList);

        Boolean result = authService.verifyByEmail(code);

        assertTrue(result && userToVerify.getIsVerified());
        verify(userRepository, times(1)).save(userToVerify);
        verify(emailValidationRepository, times(1)).delete(emailValidation);
    }

    @Test
    public void testVerifyByEmail_NoValidationsFound() {
        String code = "invalid_code";
        List<EmailValidation> emailValidationList = new ArrayList<>();
        when(emailValidationRepository.getEmailValidationsByCode(code)).thenReturn(emailValidationList);

        Boolean result = authService.verifyByEmail(code);

        assertFalse(result);
        verifyNoInteractions(userRepository);
    }
}