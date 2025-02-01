package com.dal.skillswap.service.impl;

import com.dal.skillswap.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class EmailServiceTest {

    @Mock
    private EmailService emailService;

    @MockBean
    private JavaMailSender mailSender;

    @Test
    public void testSendEmail() {
        emailService.sendEmail("", "", "", false);
    }

}
