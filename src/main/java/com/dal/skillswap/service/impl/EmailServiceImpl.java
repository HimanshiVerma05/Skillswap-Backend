package com.dal.skillswap.service.impl;

import com.dal.skillswap.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String appEmail;

    /**
     * This method allows sending Email from the site's email.
     *
     * @param mailTo        The email of the receiver.
     * @param mailSubject   The subject of the mail.
     * @param mailBody      The content inside the mail.
     */
    @Override
    @Async
    public void sendEmail(String mailTo, String mailBody, String mailSubject, boolean isHtml)
    {
        /* Creating a mime message */

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(appEmail);
            mimeMessageHelper.setTo(mailTo);
            mimeMessageHelper.setText(mailBody, isHtml);
            mimeMessageHelper.setSubject(mailSubject);

            /* Sending the mail */
            mailSender.send(mimeMessage);
        }

        /* Catch block to handle MessagingException */
        catch (MessagingException e) {
            /* Display message when exception occurred */
            log.error("Some Error occurred while sending mail");
        }
    }

}
