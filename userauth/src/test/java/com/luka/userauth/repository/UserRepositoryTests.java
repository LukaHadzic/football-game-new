package com.luka.userauth.repository;

import com.luka.userauth.config.TestClockConfig;
import com.luka.userauth.config.TestContainerDatabaseConfig;
import com.luka.userauth.entity.Role;
import com.luka.userauth.entity.User;
import com.luka.userauth.exception.exceptionclasses.UserAlreadyExistsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

@Import({TestClockConfig.class})
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTests extends TestContainerDatabaseConfig {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private Clock clock;

    private User user1;
    private User user1SameInfo;
    private Role role1;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup(){
        user1 = new User(null, "test1", "testName1", "testSurname1", "test1@email.com",
                "ProbaLozinke123!", true, LocalDateTime.now(clock));

        user1SameInfo = new User(null, "test1", "testName1", "testSurname1", "test1@email.com",
                "ProbaLozinke123!", true, LocalDateTime.now(clock));

    }

    @Test
    void shouldSaveUser(){

        User savedUser = userRepository.saveAndFlush(user1);

        Assertions.assertNotNull(savedUser.getId());

        Optional<User> savedUser2 = userRepository.findById(savedUser.getId());

        Assertions.assertTrue(savedUser2.isPresent());
        Assertions.assertNotNull(savedUser2.get().getId());
        Assertions.assertEquals(savedUser.getId(), savedUser2.get().getId());

    }

    @Test
    void shouldNotSaveSameNickname(){

        user1SameInfo.setEmail("someDifferentEmail@gmail.com");

        userRepository.save(user1);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user1SameInfo);
        });
    }

    @Test
    void shouldNotSaveSameEmail(){

        user1SameInfo.setNick("SomeDifferentNickname");

        userRepository.save(user1);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user1SameInfo);
        });
    }

    @Test
    void shouldNotSaveEmptyName(){

        user1.setName(null);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user1);
        });

    }

    @Test
    void shouldNotSaveEmptySurname(){

        user1.setSurname(null);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user1);
        });

    }

    @Test
    void shouldNotSaveEmptyNickname(){

        user1.setNick(null);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user1);
        });

    }

    @Test
    void shouldNotSaveEmptyEmail(){

        user1.setEmail(null);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user1);
        });

    }

    @Test
    void shouldNotSaveEmptyCreatedAt(){

        user1.setCreatedAt(null);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user1);
        });

    }

    @Test
    void shouldSetDefaultValueToVerified(){

        String testEmail = "testEmail1@email.com";
        boolean  defaultVerified = false;

        jdbcTemplate.update("""
                INSERT INTO users (name, surname, nick, email, password, created_at)
                VALUES (?, ?, ?, ?, ?, ?)
                """, "testname1", "testSurname1", "testNickname1", testEmail, "testPassword1@", LocalDateTime.now(clock));

        Optional<User> dbUser = userRepository.findByEmail(testEmail);

        Assertions.assertTrue(dbUser.isPresent());
        Assertions.assertEquals(defaultVerified, dbUser.get().isVerified());
    }

    @Test
    void shouldSetDefaultValueToCreatedAt(){

        String testEmail = "testEmail1@email.com";

        LocalDateTime beforeUpdate = LocalDateTime.now();

        long secondsHalfGuard = 10;

        jdbcTemplate.update("""
                INSERT INTO users (name, surname, nick, email, password, verified)
                VALUES (?, ?, ?, ?, ?, ?)
                """, "testname1", "testSurname1", "testNickname1", testEmail, "testPassword1@", true);

        LocalDateTime afterUpdate = LocalDateTime.now();

        Optional<User> dbUser = userRepository.findByEmail(testEmail);

//        System.out.println("Application time before: " + beforeUpdate);
//        System.out.println("Database time: " + dbUser.get().getCreatedAt());
//        System.out.println("Application time after: " + afterUpdate);

        Assertions.assertTrue(dbUser.isPresent());
        Assertions.assertNotNull(dbUser.get().getCreatedAt());
        Assertions.assertTrue(dbUser.get().getCreatedAt().isAfter(beforeUpdate.minusSeconds(secondsHalfGuard)));
        Assertions.assertTrue(dbUser.get().getCreatedAt().isBefore(afterUpdate.plusSeconds(secondsHalfGuard)));
    }

    @Test
    void shouldFindByEmail1(){

        userRepository.save(user1);

        Optional<User> dbUser = userRepository.findByEmail(user1.getEmail());

        Assertions.assertTrue(dbUser.isPresent());
        Assertions.assertEquals(user1.getNick(), dbUser.get().getNick());
    }

    @Test
    void shouldFindByEmail2(){

        userRepository.save(user1);

        String email = user1.getEmail();

        Optional<User> dbUser = userRepository.findByEmailOrNick(email);

        Assertions.assertTrue(dbUser.isPresent());
        Assertions.assertEquals(dbUser.get().getNick(), user1.getNick());

    }

    @Test
    void shouldFindByNickname(){

        userRepository.save(user1);

        String nick = user1.getNick();

        Optional<User> dbUser = userRepository.findByEmailOrNick(nick);

        Assertions.assertTrue(dbUser.isPresent());
        Assertions.assertEquals(dbUser.get().getEmail(), user1.getEmail());

    }

    @Test
    void shouldNotFindByEmail1(){
        String unknownEmail = "someUnknownEmail@email.com";

        userRepository.saveAndFlush(user1);

        Optional<User> dbUser = userRepository.findByEmail(unknownEmail);

        Assertions.assertFalse(dbUser.isPresent());

    }

    @Test
    void shouldNotFindByEmail2(){
        String unknownEmail = "someUnknownEmail@email.com";

        userRepository.saveAndFlush(user1);

        Optional<User> dbUser = userRepository.findByEmailOrNick(unknownEmail);

        Assertions.assertFalse(dbUser.isPresent());

    }

    @Test
    void shouldNotFindByNickname(){
        String unknownNick = "someUnknownNick";

        userRepository.saveAndFlush(user1);

        Optional<User> dbUser = userRepository.findByEmailOrNick(unknownNick);

        Assertions.assertFalse(dbUser.isPresent());

    }

    @Test
    void shouldSaveUserWithRole() {
        role1 = new Role(null, "TEST_ROLE_USER");

        roleRepository.saveAndFlush(role1);

        user1.addRole(role1);

        userRepository.saveAndFlush(user1);

        Optional<User> dbUser = userRepository.findByEmail(user1.getEmail());

        Assertions.assertTrue(dbUser.isPresent());
        Assertions.assertEquals(1, dbUser.get().getRoles().size());
        Assertions.assertEquals(role1.getName(), dbUser.get().getRoles().stream().toList().getFirst().getName());
    }

}
