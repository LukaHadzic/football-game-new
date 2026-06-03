package com.luka.userauth.service;

import com.luka.userauth.entity.EmailVerificationToken;
import com.luka.userauth.entity.User;

public interface TokenService {

    public EmailVerificationToken generateToken(User user);

    public void saveToken(EmailVerificationToken token);


}
