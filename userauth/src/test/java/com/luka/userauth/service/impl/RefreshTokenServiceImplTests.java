package com.luka.userauth.service.impl;

import com.luka.userauth.config.TestClockConfig;
import com.luka.userauth.entity.RefreshToken;
import com.luka.userauth.entity.User;
import com.luka.userauth.exception.exceptionclasses.RefreshTokenException;
import com.luka.userauth.repository.RefreshTokenRepository;
import com.luka.userauth.service.RefreshTokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

@Import(TestClockConfig.class)
@SpringBootTest
@ActiveProfiles("test")
public class RefreshTokenServiceImplTests {

    @MockitoSpyBean
    private RefreshTokenService refreshTokenService;
    @Autowired
    private Clock clock;

    @MockitoBean
    private RefreshTokenRepository refreshTokenRepository;

    private String TOKEN_STRING = "tokenLengthShouldBeExactly36CharsABC";

    private long TOKEN_VALID_FOR_DAYS;

    private long TOKEN_VALID_LENGTH;

    private User user;

    private RefreshToken dbToken;

    @BeforeEach
    void setUp(){
        TOKEN_VALID_FOR_DAYS = refreshTokenService.getREFRESH_TOKEN_VALID_FOR_DAYS();
        TOKEN_VALID_LENGTH = refreshTokenService.getTOKEN_VALID_LENGTH();
        user = new User(1L, "Player1", "NamePlayer1", "SurnamePlayer1",
                "player1@gmail.com", "Player1!", false, LocalDateTime.now(clock).minusDays(1),
                null);

        dbToken = new RefreshToken(1L, TOKEN_STRING, false, LocalDateTime.now(clock),
                LocalDateTime.now(clock).plusDays(TOKEN_VALID_FOR_DAYS), user);
    }

    @Test
    public void createSuccessTest(){

        Mockito.when(refreshTokenRepository.save(Mockito.any(RefreshToken.class)))
                .thenAnswer(i -> i.getArgument(0));

        RefreshToken r = refreshTokenService.create(user);

        System.out.println(r.getCreatedAt());
        System.out.println(r.getExpiresAt());

        Assertions.assertNotNull(r);
        Assertions.assertEquals(r.getUser().getEmail(), user.getEmail());
        Assertions.assertNotNull(r.getToken());
        Assertions.assertEquals(r.getToken().length(), TOKEN_STRING.length());
        Assertions.assertEquals(r.getCreatedAt(), dbToken.getCreatedAt());
        Assertions.assertEquals(r.getExpiresAt(), dbToken.getExpiresAt());
        Assertions.assertFalse(r.isExpired(LocalDateTime.now(clock)));
        Assertions.assertFalse(r.isRevoked());

        Mockito.verify(refreshTokenRepository).save(Mockito.any(RefreshToken.class));

    }

    @Test
    public void validateSuccessTest(){

        Mockito.when(refreshTokenRepository.findByToken(Mockito.anyString()))
                .thenReturn(Optional.of(dbToken));

        RefreshToken r = refreshTokenService.validate(TOKEN_STRING);

        System.out.println(r.getCreatedAt());
        System.out.println(r.getExpiresAt());

        Assertions.assertNotNull(r);
        Assertions.assertFalse(r.isRevoked());

        Mockito.verify(refreshTokenRepository).findByToken(Mockito.anyString());
    }

    @Test
    public void validateFailTokenNullTest(){

        RefreshToken r = refreshTokenService.validate(null);

        Assertions.assertNull(r);

    }

    @Test
    public void validateFailTokenInvalidLengthTest(){

        RefreshToken r = refreshTokenService.validate("");

        Assertions.assertNull(r);

    }

    @Test
    public void validateFailTokenNotFoundTest(){
        Mockito.when(refreshTokenRepository.findByToken(Mockito.anyString()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(RefreshTokenException.class, () -> {
           refreshTokenService.validate(TOKEN_STRING);
        });
    }

    @Test
    public void validateFailTokenExpiredTest(){
        dbToken.setExpiresAt(LocalDateTime.now(clock).minusDays(1));

        Mockito.when(refreshTokenRepository.findByToken(Mockito.anyString()))
                .thenReturn(Optional.of(dbToken));

        RefreshToken r = refreshTokenService.validate(TOKEN_STRING);
        Assertions.assertNull(r);

        Mockito.verify(refreshTokenRepository).findByToken(Mockito.anyString());
    }

    @Test
    public void validateFailTokenRevokedTest(){
        dbToken.setRevoked(true);

        Mockito.when(refreshTokenRepository.findByToken(Mockito.anyString()))
                .thenReturn(Optional.of(dbToken));

        RefreshToken r = refreshTokenService.validate(TOKEN_STRING);
        Assertions.assertNull(r);

        Mockito.verify(refreshTokenRepository).findByToken(Mockito.anyString());
    }

    @Test
    public void validateOnLoginSuccessValidTokenExistsTest(){

        Mockito.when(refreshTokenRepository.findByUserAndRevokedFalse(Mockito.any(User.class)))
                .thenReturn(Optional.of(dbToken));

        RefreshToken r = refreshTokenService.validateOnLogin(user);

        Assertions.assertNotNull(r);
        Assertions.assertEquals(r.getToken().length(), TOKEN_STRING.length());

        Mockito.verify(refreshTokenRepository).findByUserAndRevokedFalse(Mockito.any(User.class));


    }

    @Test
    public void validateOnLoginSuccessExpiredTokenExists(){

        dbToken.setExpiresAt(LocalDateTime.now(clock).minusDays(1));

        Mockito.when(refreshTokenRepository.findByUserAndRevokedFalse(Mockito.any(User.class)))
                .thenReturn(Optional.of(dbToken));
        Mockito.doNothing()
                .when(refreshTokenService)
                .revoke(Mockito.anyString());
        Mockito.doReturn(dbToken)
                .when(refreshTokenService)
                .create(user);



        RefreshToken r = refreshTokenService.validateOnLogin(user);

        Mockito.verify(refreshTokenService).revoke(dbToken.getToken());
        Mockito.verify(refreshTokenService).create(user);

        Assertions.assertNotNull(r);

    }

    @Test
    public void validateOnLoginSuccessTokenRevokedOrNotExists(){

        Mockito.when(refreshTokenRepository.findByUserAndRevokedFalse(Mockito.any(User.class)))
                .thenReturn(Optional.empty());
        Mockito.doReturn(dbToken)
                .when(refreshTokenService)
                        .create(user);

        RefreshToken r =  refreshTokenService.validateOnLogin(user);

        Mockito.verify(refreshTokenService).create(user);

        Assertions.assertNotNull(r);
    }

    @Test
    public void rotateFailTokenNotValidTest(){

        Mockito.doReturn(null)
                .when(refreshTokenService)
                .validate(dbToken.getToken());

        Assertions.assertThrows(RefreshTokenException.class, () -> {
           refreshTokenService.rotate(dbToken.getToken());
        });

        Mockito.verify(refreshTokenService).validate(dbToken.getToken());
    }

    @Test
    public void rotateSuccessTest(){

        Mockito.doReturn(dbToken)
                .when(refreshTokenService)
                .validate(dbToken.getToken());

        Mockito.doNothing()
                .when(refreshTokenService)
                .revoke(dbToken.getToken());
        Mockito.when(refreshTokenRepository.save(Mockito.any(RefreshToken.class)))
                        .thenReturn(dbToken);

        RefreshToken r = refreshTokenService.rotate(dbToken.getToken());

        Mockito.verify(refreshTokenService).validate(dbToken.getToken());
        Mockito.verify(refreshTokenService).revoke(dbToken.getToken());
        Mockito.verify(refreshTokenRepository, Mockito.times(2))
                .save(Mockito.any(RefreshToken.class));
    }

    @Test
    public void rotateFailTransactExceptionTest(){
        Mockito.doThrow(RuntimeException.class)
                .when(refreshTokenService)
                .validate(dbToken.getToken());

        Assertions.assertThrows(RefreshTokenException.class, () -> {
           refreshTokenService.rotate(dbToken.getToken());
        });
    }

    @Test
    public void revokeSuccessTest(){

        Mockito.when(refreshTokenRepository.revokeByToken(dbToken.getToken()))
                .thenReturn(1);

        refreshTokenService.revoke(dbToken.getToken());

        Mockito.verify(refreshTokenRepository).revokeByToken(dbToken.getToken());

    }

    @Test
    public void revokeFailTokenNullTest(){

        String token = null;

        Assertions.assertThrows(RefreshTokenException.class, () -> {
            refreshTokenService.revoke(token);
        });

    }

    @Test
    public void revokeFailTokenNotValidLengthTest(){

        String token = "tokenLengthIsNot36Chars";

        Assertions.assertThrows(RefreshTokenException.class, () -> {
            refreshTokenService.revoke(token);
        });

    }

    @Test
    public void revokeFailTokenNotFoundTest(){

        Mockito.when(refreshTokenRepository.revokeByToken(dbToken.getToken()))
                        .thenReturn(0);

        Assertions.assertThrows(RefreshTokenException.class, () -> {
            refreshTokenService.revoke(dbToken.getToken());
        });

        Mockito.verify(refreshTokenRepository).revokeByToken(dbToken.getToken());

    }

    @Test
    public void getTOKEN_VALID_LENGTHSuccessesTest(){
        //BAD TEST?
        long a = refreshTokenService.getTOKEN_VALID_LENGTH();

        Assertions.assertNotNull(a);
        Assertions.assertEquals(a, TOKEN_VALID_LENGTH);

    }

}
