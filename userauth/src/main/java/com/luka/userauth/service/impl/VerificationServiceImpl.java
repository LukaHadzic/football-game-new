package com.luka.userauth.service.impl;

import com.luka.userauth.entity.EmailVerificationToken;
import com.luka.userauth.entity.User;
import com.luka.userauth.exception.exceptionclasses.TokenNotValidException;
import com.luka.userauth.exception.exceptionclasses.VerificationFailedException;
import com.luka.userauth.repository.EmailVerificationTokenRepository;
import com.luka.userauth.repository.UserRepository;
import com.luka.userauth.service.TokenService;
import com.luka.userauth.service.VerificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
public class VerificationServiceImpl implements VerificationService {

    private final EmailVerificationTokenRepository emailVerifTokenRepository;
    private final UserRepository userRepository;
    private final TransactionTemplate tsTemplate;
    private final Clock clock;
    private final int TOKEN_VALID_LENGTH = 36;

    public VerificationServiceImpl(TokenService tokenService, EmailVerificationTokenRepository emailVerifTokenRepository, UserRepository userRepository, TransactionTemplate tsTemplate, Clock clock) {
        this.emailVerifTokenRepository = emailVerifTokenRepository;
        this.userRepository = userRepository;
        this.tsTemplate = tsTemplate;
        this.clock = clock;
    }

    @Override
    public User verifyUser(String token) {
        if (token == null || token.length() != TOKEN_VALID_LENGTH) throw new TokenNotValidException("Token is not valid");
        //Gather token from db and check if it is valid
        EmailVerificationToken tokenObj = emailVerifTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenNotValidException("Cannot finish registration, token is not valid."));

        if(tokenObj.isUsed()) {
            throw new TokenNotValidException("Token already used.");
        }

        if(tokenObj.getExpiresAt().isBefore(LocalDateTime.now(clock))) {
            throw new TokenNotValidException("Token is expired.");
        }

        User user = tokenObj.getUser();



        try {
            tsTemplate.execute(status -> {
                tokenObj.setUsed(true);
                user.setVerified(true);
                userRepository.save(user);
                emailVerifTokenRepository.save(tokenObj);
                return null;
            });
        }catch(Exception e) {
            throw new VerificationFailedException("Email verification failed.");
        }

        return user;
    }
}
