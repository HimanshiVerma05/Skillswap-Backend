package com.dal.skillswap.service.impl;

import com.dal.skillswap.entities.User;
import com.dal.skillswap.enums.UserRole;
import com.dal.skillswap.models.request.UserLoginRequest;
import com.dal.skillswap.models.request.UserSignupRequest;
import com.dal.skillswap.models.response.UserLoginResponse;
import com.dal.skillswap.models.response.UserSignupResponse;
import com.dal.skillswap.repository.UserRepository;
import com.dal.skillswap.service.AuthService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = true)
class AuthServiceImplTests {

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;


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
        UserSignupResponse signupResponse = authService.signup(getTestSignupRequest());
        assertEquals("test@test.com", signupResponse.getEmail());
    }

    /**
     * This tests checks that existing user should not be able to signup
     *
     * @throws Exception
     */
    @Test
    public void testSignupExistingUser() throws Exception {
        userRepository.save(getTestUser());
        assertThrows(BadCredentialsException.class, () -> authService.signup(getTestSignupRequest()),"Email already exists");
    }

    /**
     * This tests checks whether an existing user is able to login
     *
     * @throws Exception
     */
    @Test
    public void testLoginUserExists() throws Exception {
        userRepository.save(getTestUser());
        UserLoginResponse loginResponse = authService.login(getTestLoginRequest());

        assertEquals("test@test.com", loginResponse.getEmail());
    }

    /**
     * This tests checks that non-existing user shouldn't be able to login
     *
     * @throws Exception
     */
    @Test
    public void testLoginUserDoesntExist() throws Exception {
        assertThrows(BadCredentialsException.class, () -> authService.login(getTestLoginRequest()), "Bad credentials");
    }

    /**
     * This checks tests invalid credentials used for login
     *
     * @throws Exception
     */
    @Test
    public void testLoginInvalidCredentials() throws Exception {
        userRepository.save(getTestUser());
        UserLoginRequest loginRequest = new UserLoginRequest("test@test.com", "test456");
        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest), "Bad credentials");
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
