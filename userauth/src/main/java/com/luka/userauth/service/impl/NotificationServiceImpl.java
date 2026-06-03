package com.luka.userauth.service.impl;

import com.luka.userauth.service.NotificationService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/***
 *  Creates and sends simple mail message with verification link to provided email.
 *
 * @author Luka
 * @version 1.0
 * @since 17. May 2026.
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private final String FRONTEND_URL = "http://localhost:8080/auth";
    private final String PLATFORM_EMAIL = "football.simulation@ggmail.com";
    private final JavaMailSender mailSender;

    public NotificationServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /***
     * Only method in class. This method creates and sends simple mail message with verification link to provided
     * email.
     *
     * @param email
     * @param token
     */
    @Override
    public void sendVerificationEmail(String email, String token){
        String link = FRONTEND_URL + "/validate-email?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(PLATFORM_EMAIL);
        message.setSubject("Please confirm Your e-mail address");
        message.setText("Use link provided below to confirm Your e-mail address.\n\n"
                + link + "\n\nThank You for playing with us!\n\nSincerely,\nFootball simulation game team");

        mailSender.send(message);
    }

}
