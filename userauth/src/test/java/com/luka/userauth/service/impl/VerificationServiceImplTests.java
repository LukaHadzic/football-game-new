package com.luka.userauth.service.impl;

import com.luka.userauth.config.TestClockConfig;
import com.luka.userauth.entity.EmailVerificationToken;
import com.luka.userauth.entity.User;
import com.luka.userauth.exception.exceptionclasses.TokenNotValidException;
import com.luka.userauth.exception.exceptionclasses.VerificationFailedException;
import com.luka.userauth.repository.EmailVerificationTokenRepository;
import com.luka.userauth.repository.UserRepository;
import com.luka.userauth.service.VerificationService;
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
import java.util.Optional;

@Import(TestClockConfig.class)
@SpringBootTest
@ActiveProfiles("test")
public class VerificationServiceImplTests {

    @Autowired
    private VerificationService verificationService;
    @Autowired
    private Clock clock;

    @MockitoBean
    EmailVerificationTokenRepository emailVerificationTokenRepository;
    @MockitoBean
    UserRepository userRepository;

    @Test
    public void verifyFailedWrongTokenTest(){
        String providedToken = "tokenLengthShouldBeExactly36Chars!!!";

        Mockito.when(emailVerificationTokenRepository.findByToken(providedToken))
                        .thenReturn(Optional.empty());

        Assertions.assertThrows(TokenNotValidException.class, () -> {
            verificationService.verifyUser(providedToken);
        });

    }

    @Test
    public void verifyFailedNullTokenTest(){
        Assertions.assertThrows(TokenNotValidException.class, () -> {
            verificationService.verifyUser(null);
        });
    }

    @Test
    public void verifyFailedWrongFormattedTokenTest(){
        String providedToken = "stringLengthNot36";
        Assertions.assertThrows(TokenNotValidException.class, () -> {
            verificationService.verifyUser(providedToken);
        });
    }

    @Test
    public void verifySuccessTest(){

        String providedToken = "tokenLengthShouldBeExactly36Chars!!!";

        User user = new User(null, "Player1", "NamePlayer1", "SurnamePlayer1",
                "player1@gmail.com", "Player1!", false, LocalDateTime.now(clock).minusDays(1),
                null);

        EmailVerificationToken dbToken =  new EmailVerificationToken(null, providedToken,
                LocalDateTime.now(clock).minusDays(1), LocalDateTime.now(clock).plusDays(1), false, user);

        Mockito.when(emailVerificationTokenRepository.findByToken(providedToken))
                .thenReturn(Optional.of(dbToken));
        Mockito.when(userRepository.save(user))
                .thenReturn(new User(null, "Player1", "NamePlayer1", "SurnamePlayer1",
                        "player1@gmail.com", "Player1!", true,
                        LocalDateTime.now(clock).minusDays(1), null));
        Mockito.when(emailVerificationTokenRepository.save(dbToken))
                        .thenReturn(new EmailVerificationToken(null, providedToken,
                                LocalDateTime.now(clock).minusDays(1), LocalDateTime.now(clock).plusDays(1), true, user));

        User u = verificationService.verifyUser(providedToken);

        Assertions.assertNotNull(dbToken);
        Assertions.assertNotNull(u);
        Assertions.assertTrue(u.isVerified());
        Assertions.assertTrue(dbToken.isUsed());

    }

    @Test
    public void verifyFailTokenUsedTest(){
        String providedToken = "tokenLengthShouldBeExactly36Chars!!!";

        User user = new User(null, "Player1", "NamePlayer1", "SurnamePlayer1",
                "player1@gmail.com", "Player1!", true, LocalDateTime.now(clock).minusDays(1),
                null);

        EmailVerificationToken dbToken =  new EmailVerificationToken(null, providedToken,
                LocalDateTime.now(clock).minusDays(1), LocalDateTime.now(clock).plusDays(1), true, user);

        Mockito.when(emailVerificationTokenRepository.findByToken(providedToken))
                .thenReturn(Optional.of(dbToken));

        Assertions.assertThrows(TokenNotValidException.class, () -> {
            verificationService.verifyUser(providedToken);
        });

    }

    @Test
    public void verifyFailTokenExpiredTest(){
        String providedToken = "tokenLengthShouldBeExactly36Chars!!!";
        User user = new User(null, "Player1", "NamePlayer1", "SurnamePlayer1",
                "player1@gmail.com", "Player1!", true, LocalDateTime.now(clock).minusDays(1),
                null);
        EmailVerificationToken dbToken =  new EmailVerificationToken(null, providedToken,
                LocalDateTime.now(clock).minusDays(2), LocalDateTime.now(clock).minusDays(1), false, user);

        Mockito.when(emailVerificationTokenRepository.findByToken(providedToken))
                .thenReturn(Optional.of(dbToken));

        Assertions.assertThrows(TokenNotValidException.class, () -> {
            verificationService.verifyUser(providedToken);
        });

    }

    @Test
    public void verifyFailSaveException(){
        String providedToken = "tokenLengthShouldBeExactly36Chars!!!";
        User user = new User(null, "Player1", "NamePlayer1", "SurnamePlayer1",
                "player1@gmail.com", "Player1!", true, LocalDateTime.now(clock).minusDays(1),
                null);
        EmailVerificationToken dbToken =  new EmailVerificationToken(null, providedToken,
                LocalDateTime.now(clock).minusDays(1), LocalDateTime.now(clock).plusDays(1), false, user);

        Mockito.when(emailVerificationTokenRepository.findByToken(providedToken))
                .thenReturn(Optional.of(dbToken));

        Mockito.when(userRepository.save(user))
                .thenThrow(new VerificationFailedException("Email verification failed."));

        Assertions.assertThrows(VerificationFailedException.class, () -> {
            verificationService.verifyUser(providedToken);
        });
    }


}
