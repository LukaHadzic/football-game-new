package com.luka.userauth.service.impl;

import com.luka.userauth.entity.EmailVerificationToken;
import com.luka.userauth.entity.User;
import com.luka.userauth.exception.exceptionclasses.TokenNotValidException;
import com.luka.userauth.exception.exceptionclasses.UserNotFoundException;
import com.luka.userauth.repository.EmailVerificationTokenRepository;
import com.luka.userauth.service.TokenService;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {

    private final EmailVerificationTokenRepository emailVerifTokenRepository;
    private final long VERIF_TOKEN_VALID_FOR_HOURS = 24;
    private final Clock clock;
    //private final long REFRESH_TOKEN_VALID_FOR_DAYS = 7;

    public TokenServiceImpl(EmailVerificationTokenRepository emailVerifTokenRepository, Clock clock) {
        this.emailVerifTokenRepository = emailVerifTokenRepository;
        this.clock = clock;
    }

    @Override
    public EmailVerificationToken generateToken(User user) {

        if (user == null) throw new UserNotFoundException("User not found.");

        //Generate empty token object
        EmailVerificationToken tokenObj = new EmailVerificationToken();
        //Fill data into token object
        tokenObj.setToken(UUID.randomUUID().toString());
        tokenObj.setUser(user);
        tokenObj.setCreatedAt(LocalDateTime.now(clock));
        tokenObj.setExpiresAt(LocalDateTime.now(clock).plusHours(VERIF_TOKEN_VALID_FOR_HOURS));
        tokenObj.setUsed(false);

        //Return token
        return tokenObj;
    }

    @Override
    public void saveToken(EmailVerificationToken token) {
        //Save token object into db
        if (token == null){
            throw new TokenNotValidException("Token is not valid.");
        }
        emailVerifTokenRepository.save(token);
    }

}
