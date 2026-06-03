package com.luka.userauth.service.impl;

import com.luka.userauth.config.TestClockConfig;
import com.luka.userauth.entity.EmailVerificationToken;
import com.luka.userauth.entity.User;
import com.luka.userauth.exception.exceptionclasses.TokenNotValidException;
import com.luka.userauth.exception.exceptionclasses.UserNotFoundException;
import com.luka.userauth.repository.EmailVerificationTokenRepository;
import com.luka.userauth.service.TokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Clock;
import java.time.LocalDateTime;

@Import(TestClockConfig.class)
@SpringBootTest
@ActiveProfiles("test")
public class TokenServiceImplTests {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private Clock clock;

    private final int TOKEN_VALID_LENGTH = 36;
    @MockitoBean
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Test
    public void generateTokenFailedUserIsNullTest(){

        Assertions.assertThrows(UserNotFoundException.class, () -> {
            tokenService.generateToken(null);
        });

    }

    @Test
    public void generateTokenSuccessTest(){
        User user = new User(null, "Player1", "NamePlayer1", "SurnamePlayer1",
                "player1@gmail.com", "Player1!", true, LocalDateTime.now(clock),
                null);

        EmailVerificationToken e = tokenService.generateToken(user);

        Assertions.assertNotNull(e);
        Assertions.assertEquals(user, e.getUser());
        Assertions.assertEquals(TOKEN_VALID_LENGTH, e.getToken().length());
        Assertions.assertFalse(e.isUsed());
        Assertions.assertFalse(e.getExpiresAt().isBefore(LocalDateTime.now(clock)));

    }

    @Test
    public void saveTokenSuccessTest(){
        String tokenString = "tokenLengthShouldBeExactly36Chars";
        User user = new User(null, "Player1", "NamePlayer1", "SurnamePlayer1",
                "player1@gmail.com", "Player1!", false, LocalDateTime.now(clock).minusDays(1),
                null);

        EmailVerificationToken dbToken =  new EmailVerificationToken(null, tokenString,
                LocalDateTime.now(clock).minusDays(1), LocalDateTime.now(clock).plusDays(1), false, user);

        tokenService.saveToken(dbToken);

        Mockito.verify(emailVerificationTokenRepository).save(dbToken);
    }

    @Test
    public void saveTokenFailTokenNullTest(){

        Assertions.assertThrows(TokenNotValidException.class, () -> {
            tokenService.saveToken(null);
        });
    }

}
