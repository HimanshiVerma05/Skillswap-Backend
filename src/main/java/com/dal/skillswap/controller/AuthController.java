package com.dal.skillswap.controller;

import com.dal.skillswap.enums.TokenValidationMessage;
import com.dal.skillswap.models.request.ChangePasswordRequest;
import com.dal.skillswap.models.request.ForgotPasswordRequest;
import com.dal.skillswap.models.request.UserLoginRequest;
import com.dal.skillswap.models.request.UserSignupRequest;
import com.dal.skillswap.models.response.*;
import com.dal.skillswap.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.security.auth.login.AccountNotFoundException;

import static com.dal.skillswap.constants.MessageConstants.PASSWORD;

@RestController
@RequestMapping("/user/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Value("${frontend.url}")
    private String FRONTEND_URL;

    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity login(@RequestBody UserLoginRequest loginRequest) {
        UserLoginResponse loginResponse;
        try {
            loginResponse = authService.login(loginRequest);
        } catch (BadCredentialsException e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, "Invalid username or password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        return ResponseEntity.ok(loginResponse);
    }

    @ResponseBody
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity signup(@RequestBody UserSignupRequest signupRequest) {
        UserSignupResponse signupResponse;
        try {
            signupResponse = authService.signup(signupRequest);
        } catch (BadCredentialsException e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        return ResponseEntity.ok(signupResponse);
    }

    @ResponseBody
    @RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
    public ResponseEntity forgotPassword(HttpServletRequest request,
                                         @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        ForgotPasswordResponse forgotPasswordResponse;

        try {
            forgotPasswordResponse = authService.forgotPassword(request, forgotPasswordRequest);
        } catch (BadCredentialsException e) {
            forgotPasswordResponse = new ForgotPasswordResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(forgotPasswordResponse);
        } catch (Exception e) {
            forgotPasswordResponse = new ForgotPasswordResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(forgotPasswordResponse);
        }

        return ResponseEntity.ok(forgotPasswordResponse);
    }

    @GetMapping(value = "/validateToken")
    public String validateToken(@RequestParam("token") String token) {
        TokenValidationMessage message = authService.validatePasswordChangeToken(token);
        return message.toString();
    }

    @ResponseBody
    @PostMapping(value = "/changePassword")
    public ResponseEntity changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        ChangePasswordResponse changePasswordResponse;
        TokenValidationMessage message = authService.validatePasswordChangeToken(changePasswordRequest.getToken());

        if (message != TokenValidationMessage.SUCCESS) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, PASSWORD + message.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        try {
            changePasswordResponse = authService.changePassword(changePasswordRequest);
        } catch (AccountNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        return ResponseEntity.ok(changePasswordResponse);
    }

    @GetMapping(value = "/verify/{code}")
    public Object email_validation(@PathVariable(value = "code") String code){
        if (authService.verifyByEmail(code)) {
            /* TODO: Redirect the view to a static page. */
            String urlToRedirect = FRONTEND_URL+"/email_verification_success";

            return new RedirectView(urlToRedirect);
        }
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, "Email verification failed.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

}
