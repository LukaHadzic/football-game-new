package com.luka.userauth.service;

public interface NotificationService {

    public void sendVerificationEmail(String email, String token);
}
