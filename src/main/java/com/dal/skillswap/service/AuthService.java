package com.dal.skillswap.service;

import com.dal.skillswap.enums.TokenValidationMessage;
import com.dal.skillswap.models.request.ChangePasswordRequest;
import com.dal.skillswap.models.request.ForgotPasswordRequest;
import com.dal.skillswap.models.request.UserLoginRequest;
import com.dal.skillswap.models.request.UserSignupRequest;
import com.dal.skillswap.models.response.ChangePasswordResponse;
import com.dal.skillswap.models.response.ForgotPasswordResponse;
import com.dal.skillswap.models.response.UserLoginResponse;
import com.dal.skillswap.models.response.UserSignupResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;

import javax.security.auth.login.AccountNotFoundException;

public interface AuthService {

    UserLoginResponse login(UserLoginRequest loginRequest);

    UserSignupResponse signup(UserSignupRequest signupRequest) throws BadCredentialsException;

    ForgotPasswordResponse forgotPassword(HttpServletRequest request, ForgotPasswordRequest forgotPasswordRequest) throws BadCredentialsException;

    ChangePasswordResponse changePassword(ChangePasswordRequest changePasswordRequest) throws AccountNotFoundException;

    TokenValidationMessage validatePasswordChangeToken(String token);

    Boolean verifyByEmail(String code);

}
