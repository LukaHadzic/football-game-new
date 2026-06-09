package com.luka.userauth.service.impl;

import com.luka.userauth.config.TestClockConfig;
import com.luka.userauth.dto.LoginDto;
import com.luka.userauth.dto.LoginResponseDto;
import com.luka.userauth.dto.UserDto;
import com.luka.userauth.entity.EmailVerificationToken;
import com.luka.userauth.exception.exceptionclasses.UserNotFoundException;
import com.luka.userauth.mapper.UserMapper;
import com.luka.userauth.dto.RegisterDto;
import com.luka.userauth.entity.Role;
import com.luka.userauth.entity.User;
import com.luka.userauth.exception.exceptionclasses.RegistrationFailedException;
import com.luka.userauth.exception.exceptionclasses.UserAlreadyExistsException;
import com.luka.userauth.repository.RoleRepository;
import com.luka.userauth.repository.UserRepository;
import com.luka.userauth.security.util.JWTUtil;
import com.luka.userauth.service.AuthService;
import com.luka.userauth.service.NotificationService;
import com.luka.userauth.service.TokenService;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Optional;

@Import(TestClockConfig.class)
@SpringBootTest
@ActiveProfiles("test")
public class AuthServiceImplTests {

    @Autowired
    private Clock clock;
    @MockitoSpyBean
    private AuthService authService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;
    @MockitoBean
    private LoginResponseDto loginResponseDto;
    @MockitoBean
    private UserMapper userMapper;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private RoleRepository roleRepository;
    @MockitoBean
    private TokenService tokenService;
    @MockitoBean
    private NotificationService notificationService;

    private User user;
    private EmailVerificationToken verifToken;

    @BeforeEach
    void setUp(){
        user = new User(1L, "Player1", "NamePlayer1", "SurnamePlayer1",
                "player1@gmail.com", "Player1!", false, LocalDateTime.now(clock).minusDays(1));

    }

    @Nested
    class LoginTests{
        private LoginDto loginDto;
        private UserDto userDto;

        @MockitoBean
        private JWTUtil jwtUtil;

        @BeforeEach
        void setUp(){
            loginDto = new LoginDto("Nick1", "PasswordForUser1@");
        }

        @Test
        void loginFailUserNotFoundTest(){

            Mockito.when(userRepository.findByEmailOrNick(Mockito.anyString()))
                    .thenReturn(Optional.empty());

            Assertions.assertThrows(UserNotFoundException.class, () -> {
                authService.login(loginDto);
            });

            Mockito.verify(userRepository).findByEmailOrNick(Mockito.anyString());

        }

        @Test
        void loginFailWrongPassword(){

            Mockito.when(userRepository.findByEmailOrNick(Mockito.anyString()))
                    .thenReturn(Optional.of(user));

            Mockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString()))
                    .thenReturn(false);

            Assertions.assertThrows(UserNotFoundException.class, () -> {
                authService.login(loginDto);
            });

        }
        //LoginTests -> Should guard and test what if refreshTokenService.validateOnLogin fails
//                   -> Should guard and test what if refreshTokenService.rotate fails
//                   -> Should guard and test what if jwtUtil.generateToken fails

        @Test
        void loginSuccessTest(){
            Set<String> userRoles = new HashSet<>();
            userRoles.add("ROLE_USER");
            userDto = new UserDto(1L, "Player1", "NamePlayer1", "SurnamePlayer1",
                    "player1@gmail.com", userRoles);

            Mockito.when(userRepository.findByEmailOrNick(Mockito.anyString()))
                    .thenReturn(Optional.of(user));

            Mockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString()))
                    .thenReturn(true);

            Mockito.when(jwtUtil.generateToken(Mockito.eq(user))).thenReturn("ReturnedJWTTokenString");

            Mockito.when(userMapper.toUserDto(Mockito.any(User.class)))
                    .thenReturn(userDto);

            LoginResponseDto l = authService.login(loginDto);

            Assertions.assertNotNull(l);

            Mockito.verify(jwtUtil).generateToken(user);
            Mockito.verify(userMapper).toUserDto(Mockito.any(User.class));

        }

    }

    @Nested
    class RegisterTests{

        private RegisterDto registerDto;
        private Role role;
        @BeforeEach
        void setUp(){
            registerDto = new RegisterDto("a", "b", "c", "abc@example.com",
                    "Regularpassword1@");
            verifToken = new EmailVerificationToken(null, "tokenLengthShouldBeExactly36CharsABC",
                    LocalDateTime.now(clock), LocalDateTime.now(clock).plusDays(7), false, user);
            role = new Role(1L, "ROLE_USER");
        }

        @Test
        void registerFailUserExistsTest(){

            Mockito.when(userRepository.findByEmail(Mockito.anyString()))
                    .thenReturn(Optional.of(user));

            Assertions.assertThrows(UserAlreadyExistsException.class,()->{
                authService.register(registerDto);
            });

            Mockito.verify(userRepository).findByEmail(Mockito.anyString());

        }

        @Test
        void registerFailRoleErrorTest(){
            Mockito.when(userRepository.findByEmail(Mockito.anyString()))
                    .thenReturn(Optional.empty());

            Mockito.when(roleRepository.findByName(Mockito.anyString()))
                    .thenReturn(Optional.empty());

            Assertions.assertThrows(RegistrationFailedException.class,()->{
                authService.register(registerDto);
            });

            Mockito.verify(userRepository).findByEmail(Mockito.anyString());
            Mockito.verify(roleRepository).findByName(Mockito.anyString());
        }

        @Test // Nije dobro zasto se baca NPE???
        void registerFailTokenNullTest(){

            Mockito.when(userRepository.findByEmail(Mockito.anyString()))
                    .thenReturn(Optional.empty());

            Mockito.when(roleRepository.findByName(Mockito.anyString()))
                    .thenReturn(Optional.of(role));

            Mockito.when(userMapper.registerToEntity(Mockito.any(RegisterDto.class)))
                            .thenReturn(user);

            Mockito.when(tokenService.generateToken(Mockito.any(User.class)))
                    .thenReturn(null);

            Assertions.assertThrows(RegistrationFailedException.class, () -> {
                authService.register(registerDto);

                Mockito.verify(userRepository, Mockito.never()).saveAndFlush(Mockito.any(User.class));// promeniti u Mockito.never()
                Mockito.verify(tokenService, Mockito.never()).saveToken(Mockito.any(EmailVerificationToken.class)); // promeniti u Mockito.never()
                Mockito.verify(notificationService, Mockito.never())
                        .sendVerificationEmail(Mockito.anyString(), Mockito.anyString());
            });

        }

        @Test
        void registerFailSaveErrorTest(){

            Mockito.when(userRepository.findByEmail(Mockito.anyString()))
                    .thenReturn(Optional.empty());

            Mockito.when(roleRepository.findByName(Mockito.anyString()))
                    .thenReturn(Optional.of(role));

            Mockito.when(userMapper.registerToEntity(Mockito.any(RegisterDto.class)))
                    .thenReturn(user);

            Mockito.doThrow(new RuntimeException())
                    .when(userRepository)
                    .saveAndFlush(Mockito.any(User.class));

            Mockito.doReturn(verifToken)
                    .when(tokenService)
                    .generateToken(Mockito.any(User.class));

            Mockito.when(userRepository.save(Mockito.any(User.class)))
                    .thenReturn(user);

            Assertions.assertThrows(RegistrationFailedException.class, () -> {
                authService.register(registerDto);

                Mockito.verify(tokenService, Mockito.never()).saveToken(verifToken);
                Mockito.verify(notificationService, Mockito.never())
                        .sendVerificationEmail(Mockito.anyString(), Mockito.anyString());
            });

        }

        @Test
        void registerSuccessTest(){

            Mockito.when(userRepository.findByEmail(Mockito.anyString()))
                    .thenReturn(Optional.empty());

            Mockito.when(roleRepository.findByName(Mockito.anyString()))
                    .thenReturn(Optional.of(role));

            Mockito.when(userMapper.registerToEntity(Mockito.any(RegisterDto.class)))
                    .thenReturn(user);

            Mockito.doReturn(verifToken)
                    .when(tokenService)
                    .generateToken(Mockito.any(User.class));

            Mockito.when(userRepository.saveAndFlush(Mockito.any(User.class)))
                    .thenReturn(user);

            Mockito.doNothing()
                    .when(tokenService)
                    .saveToken(verifToken);

            Mockito.doNothing()
                    .when(notificationService)
                    .sendVerificationEmail(user.getEmail(), verifToken.getToken());

            String s = authService.register(registerDto);

            Assertions.assertNotNull(s);
            Assertions.assertFalse(s.isEmpty());

        }

    }

}

class RegisterDtoAggregator implements ArgumentsAggregator {

    @Override
    public @Nullable Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) throws ArgumentsAggregationException {
        return new RegisterDto(accessor.getString(0), accessor.getString(1), accessor.getString(2),
                accessor.getString(3), accessor.getString(4));
    }
}
