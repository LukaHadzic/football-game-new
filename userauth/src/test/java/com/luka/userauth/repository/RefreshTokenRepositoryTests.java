package com.luka.userauth.repository;

import com.luka.userauth.config.TestContainerDatabaseConfig;
import com.luka.userauth.entity.RefreshToken;
import com.luka.userauth.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

@DataJpaTest
@ActiveProfiles( "test" )
@AutoConfigureTestDatabase( replace = AutoConfigureTestDatabase.Replace.NONE )
public class RefreshTokenRepositoryTests extends TestContainerDatabaseConfig {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User user;
    private RefreshToken refreshToken;
    private static final long TOKEN_VALID_FOR_DAYS = 7;

    @BeforeEach
    void setUp() {
        user = new User(null, "test1", "testName1", "testSurname1", "test1@email.com",
                "ProbaLozinke123!", true, LocalDateTime.now());

        userRepository.saveAndFlush(user);

        refreshToken = new RefreshToken(null, "someValidTokenString", false, LocalDateTime.now(),
                LocalDateTime.now().plusDays(TOKEN_VALID_FOR_DAYS), user);
    }

    @Test
    void shouldSaveTokenNullId() {

        RefreshToken savedToken = refreshTokenRepository.saveAndFlush(refreshToken);;

        Optional<RefreshToken> dbToken = refreshTokenRepository.findByToken(savedToken.getToken());

        Assertions.assertTrue(dbToken.isPresent());
        Assertions.assertNotNull(dbToken.get().getId());
        Assertions.assertEquals(savedToken.getToken(), dbToken.get().getToken());

    }

    @Test
    void shouldNotSaveSameTokenString() {

        refreshTokenRepository.saveAndFlush(refreshToken);

        RefreshToken duplicate = new RefreshToken(null, "someValidTokenString", false, LocalDateTime.now(),
                LocalDateTime.now().plusDays(TOKEN_VALID_FOR_DAYS), user);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            refreshTokenRepository.saveAndFlush(duplicate);
        });

    }

    @Test
    void shouldNotSaveNullTokenString() {

        refreshToken.setToken(null);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            refreshTokenRepository.saveAndFlush(refreshToken);
        });

    }

    @Test
    void shouldNotSaveNullCreatedAt() {

        refreshToken.setCreatedAt(null);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            refreshTokenRepository.saveAndFlush(refreshToken);
        });

    }

    @Test
    void shouldNotSaveNullExpiresAt() {

        refreshToken.setExpiresAt(null);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            refreshTokenRepository.saveAndFlush(refreshToken);
        });

    }

    @Test
    void shouldNotSaveNullUser() {

        refreshToken.setUser(null);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            refreshTokenRepository.saveAndFlush(refreshToken);
        });

    }

    @Test
    void shouldSetDefaultValueToRevoked() {

        jdbcTemplate.update("""
                                INSERT INTO refresh_token (token, created_at, expires_at, user_id)
                                VALUES (?, ?, ?, ?)""", refreshToken.getToken(), refreshToken.getCreatedAt(),
                                    refreshToken.getExpiresAt(), refreshToken.getUser().getId());

        Optional<RefreshToken> dbToken = refreshTokenRepository.findByToken(refreshToken.getToken());

        Assertions.assertTrue(dbToken.isPresent());
        Assertions.assertFalse(dbToken.get().isRevoked());

    }

    @Test
    void shouldSetDefaultValueToCreatedAt() {

        long secondsHalfGuard = 10;

        LocalDateTime beforeInsert = LocalDateTime.now();

        jdbcTemplate.update("""
                                INSERT INTO refresh_token (token, user_id)
                                VALUES (?, ?)""", refreshToken.getToken(), refreshToken.getUser().getId());

        LocalDateTime afterInsert = LocalDateTime.now();

        Optional<RefreshToken> dbToken = refreshTokenRepository.findByToken(refreshToken.getToken());

        Assertions.assertTrue(dbToken.isPresent());
        Assertions.assertNotNull(dbToken.get().getCreatedAt());

        LocalDateTime refreshTokenCreatedAt =  dbToken.get().getCreatedAt();

        Assertions.assertTrue(refreshTokenCreatedAt.isAfter(beforeInsert.minusSeconds(secondsHalfGuard)));
        Assertions.assertTrue(refreshTokenCreatedAt.isBefore(afterInsert.plusSeconds(secondsHalfGuard)));

        Assertions.assertNotNull(dbToken.get().getExpiresAt());

        LocalDateTime refreshTokenExpiresAt = dbToken.get().getExpiresAt();

        Assertions.assertEquals(refreshTokenCreatedAt.plusDays(TOKEN_VALID_FOR_DAYS), refreshTokenExpiresAt);

    }

    @Test
    void shouldSetDefaultValueToExpiresAt() {

        jdbcTemplate.update("""
                                INSERT INTO refresh_token (token, user_id)
                                VALUES (?, ?)""", refreshToken.getToken(), refreshToken.getUser().getId());

        Optional<RefreshToken> dbToken = refreshTokenRepository.findByToken(refreshToken.getToken());

        Assertions.assertTrue(dbToken.isPresent());
        Assertions.assertNotNull(dbToken.get().getCreatedAt());
        Assertions.assertNotNull(dbToken.get().getExpiresAt());

        LocalDateTime createdAt =  dbToken.get().getCreatedAt();
        LocalDateTime expiresAt = dbToken.get().getExpiresAt();

        Assertions.assertEquals(createdAt.plusDays(TOKEN_VALID_FOR_DAYS), expiresAt);

    }

    @Test
    void shouldSetProperUserId() {

        refreshTokenRepository.saveAndFlush(refreshToken);

        Optional<User> savedUser = userRepository.findByEmailOrNick(user.getNick());

        Assertions.assertTrue(savedUser.isPresent());
        Assertions.assertNotNull(savedUser.get().getId());

        Optional<RefreshToken> dbToken = refreshTokenRepository.findByToken(refreshToken.getToken());

        Assertions.assertTrue(dbToken.isPresent());

        Assertions.assertEquals(savedUser.get().getId(), dbToken.get().getUser().getId());

    }

    @Test
    void shouldFindByToken() {

        RefreshToken savedToken = refreshTokenRepository.saveAndFlush(refreshToken);

        Optional<RefreshToken> dbToken = refreshTokenRepository.findByToken(refreshToken.getToken());

        Assertions.assertTrue(dbToken.isPresent());
        Assertions.assertNotNull(dbToken.get().getId());
        Assertions.assertEquals(savedToken.getId(), dbToken.get().getId());
        Assertions.assertEquals(savedToken.getToken(), dbToken.get().getToken());

    }

    @Test
    void shouldNotFindByTokenString() {
        RefreshToken savedToken = refreshTokenRepository.saveAndFlush(refreshToken);

        String unknownTokenString = "unknownToken";

        Optional<RefreshToken> dbToken = refreshTokenRepository.findByToken(unknownTokenString);

        Assertions.assertNotNull(savedToken.getId());
        Assertions.assertNotNull(savedToken.getToken());
        Assertions.assertFalse(dbToken.isPresent());

    }

    @Test
    void shouldFindByTokenString() {

        RefreshToken savedToken = refreshTokenRepository.saveAndFlush(refreshToken);

        Optional<RefreshToken> dbToken = refreshTokenRepository.findByToken(refreshToken.getToken());

        Assertions.assertTrue(dbToken.isPresent());
        Assertions.assertNotNull(dbToken.get().getId());
        Assertions.assertEquals(savedToken.getId(), dbToken.get().getId());
        Assertions.assertEquals(savedToken.getToken(), dbToken.get().getToken());

    }

    @Test
    void shouldNotFindTokenByUserAndRevokedFalseWrongUser() {
        User user2 = new User(null, "test2", "testName2", "testSurname2", "test2@email.com",
                "ProbaLozinke123!", true, LocalDateTime.now());

        User savedUser = userRepository.save(user2);

        Assertions.assertNotNull(savedUser.getId());

        RefreshToken savedToken = refreshTokenRepository.saveAndFlush(refreshToken);

        Assertions.assertNotNull(savedToken.getId());
        Assertions.assertNotNull(savedToken.getUser());

        Optional<RefreshToken> dbToken = refreshTokenRepository.findByUserAndRevokedFalse(user2);

        Assertions.assertFalse(dbToken.isPresent());

    }

    @Test
    void shouldNotFindTokenByUserAndRevokedFalseAllRevoked() {

        refreshToken.setRevoked(true);

        RefreshToken savedToken = refreshTokenRepository.saveAndFlush(refreshToken);

        Assertions.assertNotNull(savedToken.getId());
        Assertions.assertNotNull(savedToken.getUser());

        Optional<RefreshToken> dbToken = refreshTokenRepository.findByUserAndRevokedFalse(user);

        Assertions.assertFalse(dbToken.isPresent());

    }

    @Test
    void shouldFindTokenByUserAndRevokedFalse() {

        RefreshToken savedToken = refreshTokenRepository.saveAndFlush(refreshToken);

        Assertions.assertNotNull(savedToken.getId());
        Assertions.assertNotNull(savedToken.getUser());

        Optional<RefreshToken> dbToken = refreshTokenRepository.findByUserAndRevokedFalse(user);

        Assertions.assertTrue(dbToken.isPresent());
        Assertions.assertNotNull(dbToken.get().getId());
        Assertions.assertEquals(savedToken.getId(), dbToken.get().getId());

    }

    @Test
    void shouldRevokeByToken() {
        RefreshToken savedToken = refreshTokenRepository.saveAndFlush(refreshToken);

        Assertions.assertNotNull(savedToken.getId());

        int result = refreshTokenRepository.revokeByToken(savedToken.getToken());

        Assertions.assertEquals(1, result);
    }

//    @Test
//    void shouldNotRevokeByTokenAlreadyRevoked() {
//
//        refreshToken.setRevoked(true);
//
//        RefreshToken savedToken = refreshTokenRepository.saveAndFlush(refreshToken);
//
//        Assertions.assertNotNull(savedToken.getId());
//
//        int result = refreshTokenRepository.revokeByToken(savedToken.getToken());
//
//        Assertions.assertEquals(0, result);
//    }

    @Test
    void shouldNotRevokeByTokenWrongTokenString() {

        String unknownTokenString = "unknownToken";

        RefreshToken savedToken = refreshTokenRepository.saveAndFlush(refreshToken);

        Assertions.assertNotNull(savedToken.getId());

        int result = refreshTokenRepository.revokeByToken(unknownTokenString);

        Assertions.assertEquals(0, result);


    }

}
