package com.luka.userauth.service.impl;

import com.luka.userauth.config.TestClockConfig;
import com.luka.userauth.dto.LoginDto;
import com.luka.userauth.dto.LoginResponseDto;
import com.luka.userauth.dto.UserDto;
import com.luka.userauth.exception.exceptionclasses.UserNotFoundException;
import com.luka.userauth.mapper.UserMapper;
import com.luka.userauth.dto.RegisterDto;
import com.luka.userauth.entity.Role;
import com.luka.userauth.entity.User;
import com.luka.userauth.exception.exceptionclasses.RegistrationFailedException;
import com.luka.userauth.exception.exceptionclasses.UserAlreadyExistsException;
import com.luka.userauth.repository.RoleRepository;
import com.luka.userauth.repository.UserRepository;
import com.luka.userauth.service.AuthService;
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

    private User user;

    @BeforeEach
    void setUp(){
        user = new User(1L, "Player1", "NamePlayer1", "SurnamePlayer1",
                "player1@gmail.com", "Player1!", false, LocalDateTime.now(clock).minusDays(1),
                null);

    }

    @Nested
    class LoginTests{
        private LoginDto loginDto;
        private UserDto userDto;

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

            Mockito.when(userMapper.toUserDto(Mockito.any(User.class)))
                    .thenReturn(userDto);

            LoginResponseDto l = authService.login(loginDto);

            Assertions.assertNotNull(l);

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

    }

}

class RegisterDtoAggregator implements ArgumentsAggregator {

    @Override
    public @Nullable Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) throws ArgumentsAggregationException {
        return new RegisterDto(accessor.getString(0), accessor.getString(1), accessor.getString(2),
                accessor.getString(3), accessor.getString(4));
    }
}
