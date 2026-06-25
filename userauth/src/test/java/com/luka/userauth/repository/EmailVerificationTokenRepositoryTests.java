package com.luka.userauth.repository;

import com.luka.userauth.config.TestClockConfig;
import com.luka.userauth.entity.EmailVerificationToken;
import com.luka.userauth.entity.User;
import com.luka.userauth.service.TokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Import(TestClockConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase( replace = AutoConfigureTestDatabase.Replace.NONE)
public class EmailVerificationTokenRepositoryTests {

    @Autowired
    private EmailVerificationTokenRepository emailVerifTokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private EmailVerificationToken emailToken;
    private EmailVerificationToken emailTokenDuplicate;
    private User user;
    private static final long TOKEN_VALID_FOR_HOURS = 24;

    @BeforeEach
    void setUp() {
        user = new User(null, "test1", "testName1", "testSurname1", "test1@email.com",
                "ProbaLozinke123!", true, LocalDateTime.now());

        emailToken = new EmailVerificationToken(null, "UniqueString1", LocalDateTime.now(),
                LocalDateTime.now().plusHours(TOKEN_VALID_FOR_HOURS), false, user);

        emailTokenDuplicate = new EmailVerificationToken(null, "UniqueString1", LocalDateTime.now(),
                LocalDateTime.now().plusHours(TOKEN_VALID_FOR_HOURS), false, user);
    }

    @Test
    void shouldSaveEmailToken() {

        userRepository.save(user);

        EmailVerificationToken savedToken = emailVerifTokenRepository.saveAndFlush(emailToken);

        Assertions.assertNotNull(savedToken.getId());

        Optional<EmailVerificationToken> dbEmailToken = emailVerifTokenRepository.findByToken(emailToken.getToken());

        Assertions.assertTrue(dbEmailToken.isPresent());
        Assertions.assertNotNull(dbEmailToken.get().getId());
        Assertions.assertEquals(savedToken.getId(), dbEmailToken.get().getId());

    }

    @Test
    void shouldNotSaveSameToken() {

        userRepository.save(user);

        emailVerifTokenRepository.saveAndFlush(emailToken);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            emailVerifTokenRepository.saveAndFlush(emailTokenDuplicate);
        });
    }

    @Test
    void shouldNotSaveNullCreatedAt() {

        userRepository.save(user);

        emailToken.setCreatedAt(null);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            emailVerifTokenRepository.saveAndFlush(emailToken);
        });

    }

    @Test
    void shouldNotSaveNullExpiresAt() {

        userRepository.save(user);

        emailToken.setExpiresAt(null);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            emailVerifTokenRepository.saveAndFlush(emailToken);
        });

    }

    @Test
    void shouldSaveTokenNullId() {

        userRepository.save(user);

        EmailVerificationToken savedToken = emailVerifTokenRepository.saveAndFlush(emailToken);

        Optional<EmailVerificationToken> dbToken = emailVerifTokenRepository.findByToken(emailToken.getToken());

        Assertions.assertTrue(dbToken.isPresent());
        Assertions.assertNotNull(dbToken.get().getId());
        Assertions.assertEquals(savedToken.getId(), dbToken.get().getId());
    }

    @Test
    void shouldSetDefaultValueToCreatedAtAndExpiresAt() {

        User savedUser =  userRepository.saveAndFlush(user);

        LocalDateTime beforeSave = LocalDateTime.now();

        long secondsHalfGuard = 10;

        jdbcTemplate.update("""
                            INSERT INTO email_verification_token (token, used, user_id) 
                            VALUES (?, ?, ?)
                            """, emailToken.getToken(), false, savedUser.getId());

        LocalDateTime afterSave = LocalDateTime.now();

        Optional<EmailVerificationToken> dbToken = emailVerifTokenRepository.findByToken(emailToken.getToken());

        Assertions.assertTrue(dbToken.isPresent());
        Assertions.assertNotNull(dbToken.get().getId());

        LocalDateTime tokenCreatedAt = dbToken.get().getCreatedAt();

        Assertions.assertTrue(tokenCreatedAt.isAfter(beforeSave.minusSeconds(secondsHalfGuard)));
        Assertions.assertTrue(tokenCreatedAt.isBefore(tokenCreatedAt.plusSeconds(secondsHalfGuard)));

        Assertions.assertNotNull(dbToken.get().getExpiresAt());

        LocalDateTime expiresAt = dbToken.get().getExpiresAt();
        Assertions.assertEquals(tokenCreatedAt.plusHours(TOKEN_VALID_FOR_HOURS), expiresAt);
    }

    @Test
    void shouldSetDefaultValueForUsed() {

        User savedUser = userRepository.saveAndFlush(user);

        jdbcTemplate.update("""
                            INSERT INTO email_verification_token (token, user_id)
                            VALUES(?, ?)""", emailToken.getToken(), savedUser.getId());

        Optional<EmailVerificationToken> savedToken = emailVerifTokenRepository.findByToken(emailToken.getToken());

        Assertions.assertTrue(savedToken.isPresent());
        Assertions.assertFalse(savedToken.get().isUsed());

    }

    @Test
    void shouldSetProperUserIdValue() {

        User savedUser = userRepository.saveAndFlush(user);

        emailVerifTokenRepository.saveAndFlush(emailToken);

        Optional<EmailVerificationToken> dbToken = emailVerifTokenRepository.findByToken(emailToken.getToken());

        Assertions.assertTrue(dbToken.isPresent());
        Assertions.assertNotNull(dbToken.get().getUser());
        Assertions.assertEquals(savedUser.getId(), dbToken.get().getUser().getId());

    }

    @Test
    void shouldNotSaveNullTokenString() {

        userRepository.save(user);

        emailToken.setToken(null);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            emailVerifTokenRepository.saveAndFlush(emailToken);
        });

    }

    @Test
    void shouldNotSaveNullUser() {

        userRepository.save(user);

        emailToken.setUser(null);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {

            emailVerifTokenRepository.saveAndFlush(emailToken);

        });

    }

    @Test
    void shouldFindTokenByTokenString() {

        userRepository.save(user);

        EmailVerificationToken savedToken = emailVerifTokenRepository.saveAndFlush(emailToken);

        Optional<EmailVerificationToken> dbToken = emailVerifTokenRepository.findByToken(emailToken.getToken());

        Assertions.assertTrue(dbToken.isPresent());
        Assertions.assertNotNull(dbToken.get().getId());
        Assertions.assertEquals(savedToken.getId(), dbToken.get().getId());

    }

    @Test
    void shouldReturnEmptyByTokenString() {

        userRepository.save(user);

        EmailVerificationToken savedToken = emailVerifTokenRepository.saveAndFlush(emailToken);

        String unknownToken = emailToken.getToken() + "shouldNotFind";

        Optional<EmailVerificationToken> dbToken = emailVerifTokenRepository.findByToken(unknownToken);

        Assertions.assertTrue(dbToken.isEmpty());

    }


}
