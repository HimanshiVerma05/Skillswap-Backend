package com.dal.skillswap.service.impl;

import com.dal.skillswap.entities.EmailValidation;
import com.dal.skillswap.entities.PasswordResetToken;
import com.dal.skillswap.enums.TokenValidationMessage;
import com.dal.skillswap.enums.UserRole;
import com.dal.skillswap.models.request.*;
import com.dal.skillswap.models.response.ChangePasswordResponse;
import com.dal.skillswap.models.response.ForgotPasswordResponse;
import com.dal.skillswap.models.response.UserLoginResponse;
import com.dal.skillswap.models.response.UserSignupResponse;
import com.dal.skillswap.models.util.Coordinate;
import com.dal.skillswap.repository.EmailValidationRepository;
import com.dal.skillswap.repository.PasswordTokenRepository;
import com.dal.skillswap.repository.UserRepository;
import com.dal.skillswap.service.AuthService;
import com.dal.skillswap.service.EmailService;
import com.dal.skillswap.service.LocationService;
import com.dal.skillswap.utils.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.security.auth.login.AccountNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.dal.skillswap.constants.MessageConstants.*;
import static com.dal.skillswap.constants.PathConstants.FRONTEND_PORT;
import static com.dal.skillswap.constants.PathConstants.RESET_PASSWORD_PATH;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private LocationService locationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordTokenRepository passwordTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtils jwtUtil;

    @Autowired
    private EmailValidationRepository emailValidationRepository;

    @Autowired
    EmailService emailService;

    @Value("${message.resetPassword}")
    private String resetPasswordEmail;

    /**
     * Login the user
     * @param loginReq
     * @return
     */
    public UserLoginResponse login(UserLoginRequest loginReq) {
        String reqEmail = loginReq.getEmail();
        String reqPassword = loginReq.getPassword();
        UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(reqEmail, reqPassword);
        Authentication authentication = authenticationManager.authenticate(userToken);
        String email = authentication.getName();
        User user = new User(email, "");
        String token = jwtUtil.createToken(user);
        UserLoginResponse loginResponse = new UserLoginResponse(email, token);

        return loginResponse;
    }

    /**
     * Signup the user
     * @param signupRequest
     * @return
     * @throws BadCredentialsException
     */
    public UserSignupResponse signup(UserSignupRequest signupRequest) throws BadCredentialsException {
        String pincode = signupRequest.getPincode();
        Coordinate coordinate = locationService.convertLocationToCoordinates(pincode);
        com.dal.skillswap.entities.User user = new com.dal.skillswap.entities.User(signupRequest.getFirstName(),
                signupRequest.getLastName(), signupRequest.getEmail(), signupRequest.getMobile(), UserRole.USER,
                signupRequest.getPincode(), passwordEncoder.encode(signupRequest.getPassword()), coordinate.getLatitude(),
                coordinate.getLongitude());

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new BadCredentialsException(EMAIL_ALREADY_EXISTS);
        }

        userRepository.save(user);


        /*Generate JWT token to keep the user authenticated*/
        User userResp = new User(signupRequest.getEmail(), signupRequest.getPassword());
        String token = jwtUtil.createToken(userResp);
        UserSignupResponse signupResponse = new UserSignupResponse(signupRequest.getEmail(), token);

        /* Email validation */
        try {
            sendVerificationEmail(user);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return signupResponse;
    }

    /**
     * Send a reset password email
     * @param request
     * @param forgotPasswordRequest
     * @return
     * @throws BadCredentialsException
     */
    public ForgotPasswordResponse forgotPassword(HttpServletRequest request, ForgotPasswordRequest forgotPasswordRequest) throws BadCredentialsException {
        String email = forgotPasswordRequest.getEmail();
        Optional<com.dal.skillswap.entities.User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new BadCredentialsException(EMAIL_IS_NOT_REGISTERED);
        }

        /*Email is present, generate a token and save it*/
        String newPasswordResetToken = UUID.randomUUID().toString();

        PasswordResetToken passwordResetToken = user.get().getPasswordResetToken();
        if (passwordResetToken == null) {
            passwordResetToken = new PasswordResetToken();
            passwordResetToken.setUser(user.get());
        }
        passwordResetToken.setToken(newPasswordResetToken);
        passwordResetToken.setTokenExpiryDate();

        passwordTokenRepository.save(passwordResetToken);

        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().port(FRONTEND_PORT).build().toUriString();
        String url = baseUrl + RESET_PASSWORD_PATH + passwordResetToken.getToken();

        emailService.sendEmail(user.get().getEmail(), resetPasswordEmail + "\n" + url, RESET_PASSWORD, false);
        return new ForgotPasswordResponse(PASSWORD_RESET_URL_SENT);
    }

    /**
     * Change a user's password
     * @param changePasswordRequest
     * @return
     * @throws AccountNotFoundException
     */
    public ChangePasswordResponse changePassword(ChangePasswordRequest changePasswordRequest) throws AccountNotFoundException {
        String token = changePasswordRequest.getToken();
        Optional<com.dal.skillswap.entities.User> user = passwordTokenRepository.findUserByToken(token);
        if (user.isPresent()) {
            String encodedPassword = passwordEncoder.encode(changePasswordRequest.getPassword());
            user.get().setPassword(encodedPassword);
            userRepository.save(user.get());
        } else {
            throw new AccountNotFoundException(USER_NOT_FOUND);
        }
        return new ChangePasswordResponse(PASSWORD_CHANGED_SUCCESSFULLY);
    }

    /**
     * Validates the password reset token
     * @param token
     * @return
     */
    public TokenValidationMessage validatePasswordChangeToken(String token) {
        Optional<PasswordResetToken> passwordResetToken = passwordTokenRepository.findByToken(token);

        if (passwordResetToken.isEmpty()) {
            return TokenValidationMessage.INVALID;
        } else if (LocalDateTime.now().isAfter(passwordResetToken.get().getTokenExpiryDate())) {
            return TokenValidationMessage.EXPIRED;
        }
        return TokenValidationMessage.SUCCESS;
    }


    /**
     * This method sends verification mail upon signup of a user.
     *
     * @param user The user entity who needs to be verified.
     */
    private void sendVerificationEmail(com.dal.skillswap.entities.User user) {

        String verificationUrl = generateVerificationUrl(user);

        String emailContent = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Welcome to SkillSwap</title>\n" +
                "</head>\n" +
                "<body style=\"font-family: 'Arial', sans-serif;\">\n" +
                "\n" +
                "<div style=\"max-width: 600px; margin: 0 auto; padding: 20px; text-align: center; background-color: #f5f5f5; border-radius: 10px;\">\n" +
                "\n" +
                "    <h2 style=\"color: #333;\">Welcome to SkillSwap!</h2>\n" +
                "\n" +
                "    <p>\n" +
                "        Thank you for registering on our site. We are excited to have you as part of our community.\n" +
                "    </p>\n" +
                "\n" +
                "    <p>\n" +
                "        To verify your account, please click the following link:\n" +
                "    </p>\n" +
                "\n" +
                "    <p>\n" +
                "        <a href=\" " + verificationUrl + "\" style=\"display: inline-block; padding: 10px 20px; background-color: #007bff; color: #fff; text-decoration: none; border-radius: 5px;\">Click here to confirm</a>\n" +
                "    </p>\n" +
                "\n" +
                "    <p>\n" +
                "        If the above link doesn't work, you can copy and paste the following URL into your browser:\n" +
                "        <br>\n" +
                "        <code>" + verificationUrl + "</code>\n" +
                "    </p>\n" +
                "\n" +
                "    <p>\n" +
                "        If you have any questions or need assistance, feel free to contact us.\n" +
                "    </p>\n" +
                "\n" +
                "    <p>\n" +
                "        Best regards,<br>\n" +
                "        SkillSwap Team\n" +
                "    </p>\n" +
                "\n" +
                "</div>\n" +
                "\n" +
                "</body>\n" +
                "</html>";

        String emailSubject = "Verify you account for SkillSwap!";

        emailService.sendEmail(user.getEmail(), emailContent, emailSubject, true);

    }

    /**
     * @return The url for verifying the user.
     */
    private String generateVerificationUrl(com.dal.skillswap.entities.User user) {
        String randomUUID = UUID.randomUUID().toString();

        /* Author: https://stackoverflow.com/users/3307037/enoobong
         Answered in: https://stackoverflow.com/questions/5012525/get-root-base-url-in-spring-mvc
         Accessed on: 2024/02/15 */
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();


        EmailValidation emailValidation = new EmailValidation();
        emailValidation.setCode(randomUUID);
        emailValidation.setUser(user);

        emailValidationRepository.save(emailValidation);

        String verificationUrl = baseUrl +
                "/user/auth/verify/" +
                randomUUID;

        return verificationUrl;
    }

    /**
     * Service method to verify the user using the email sent.
     *
     * @param code The UUID sent through the mail.
     * @return True if the user verification was successful. False otherwise.
     */
    public Boolean verifyByEmail(String code) {

        List<EmailValidation> emailValidationList = emailValidationRepository.getEmailValidationsByCode(code);

        if (emailValidationList.isEmpty()){
            return false;
        }

        for (EmailValidation emailValidation : emailValidationList) {
            com.dal.skillswap.entities.User userToVerify = emailValidation.getUser();
            userToVerify.setIsVerified(true);
            /* User is approved and verified. */
            userRepository.save(userToVerify);

            /* The email validation code is deleted. */
            emailValidationRepository.delete(emailValidation);
        }

        return true;
    }
}
