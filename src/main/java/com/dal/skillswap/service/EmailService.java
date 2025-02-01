package com.dal.skillswap.service;

public interface EmailService {

    void sendEmail(String email, String emailContent, String emailSubject, boolean isHtml);
}
