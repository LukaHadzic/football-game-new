package com.luka.userauth.service;

import com.luka.userauth.entity.User;

public interface VerificationService {

    public User verifyUser(String token);
}
