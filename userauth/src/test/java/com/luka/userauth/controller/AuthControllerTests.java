package com.luka.userauth.controller;

import com.luka.userauth.controller.AuthController;
import com.luka.userauth.dto.LoginDto;
import com.luka.userauth.dto.LoginResponseDto;
import com.luka.userauth.dto.UserDto;
import com.luka.userauth.entity.Role;
import com.luka.userauth.entity.User;
import com.luka.userauth.exception.exceptionclasses.UserNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import com.luka.userauth.config.TestClockConfig;
import com.luka.userauth.dto.RegisterDto;
import com.luka.userauth.exception.GlobalExceptionHandler;
import com.luka.userauth.exception.exceptionclasses.RegistrationFailedException;
import com.luka.userauth.mapper.UserMapper;
import com.luka.userauth.service.AuthService;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

import java.time.Clock;
import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Import({TestClockConfig.class, UserMapper.class})
@WebMvcTest(controllers = AuthController.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {AuthController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTests {

    //Terminal komanda za testiranje @Nested klase sa paralelnim procesorskim izvrsavanjem:
    // mvn test -Dtest=AuthControllerTests$LogoutTests -T 1C

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Clock clock;

    @Autowired
    private UserMapper userMapper;

    @MockitoBean
    private AuthService authService;

    @Nested
    class LoginTests{
        private LoginDto loginDto;
        private User user;
        private UserDto userDto;
        private String token;
        private HttpServletResponse httpResp;
        private LoginResponseDto loginResponseDto;

        @BeforeEach
        void setup(){
            token = "SomeValidToken";

            user = new User(1L, "userNick1", "user1Name", "user1Surname",
                    "user1@mail.com", "ValidPassword123@", false, LocalDateTime.now(clock));

            Role role = new Role(1L, "ROLE_USER");
            user.addRole(role);

            userDto = userMapper.toUserDto(user);

            loginResponseDto = new LoginResponseDto(userDto);
        }


        @ParameterizedTest
        @CsvFileSource(resources = "/mock_bad_login_requests.csv", useHeadersInDisplayName = true)
        void loginFailInvalidLoginRequestsTest(@AggregateWith(LoginDtoAggregator.class) LoginDto loginDto) throws  Exception{
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginDto)))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());

            Mockito.verify(authService, Mockito.never()).login(loginDto);
        }

        @ParameterizedTest
        @CsvFileSource(resources = "/mock_valid_login_requests.csv", useHeadersInDisplayName = true)
        void loginFailAuthenticationErrorTest(@AggregateWith(LoginDtoAggregator.class) LoginDto loginDto) throws Exception {
            Mockito.when(authService.login(loginDto))
                    .thenThrow(UserNotFoundException.class);

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginDto)))
                    .andExpect(MockMvcResultMatchers.status().isNotFound());

            Mockito.verify(authService).login(loginDto);
        }

        @ParameterizedTest
        @CsvFileSource(resources = "/mock_valid_login_requests.csv", useHeadersInDisplayName = true)
        void loginSuccessTest(@AggregateWith(LoginDtoAggregator.class) LoginDto loginDto) throws Exception {
            Mockito.when(authService.login(loginDto))
                    .thenReturn(loginResponseDto);

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginDto)))
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers
                            .jsonPath("$.userDto.nick").value(userDto.getNick()))
                    .andExpect(MockMvcResultMatchers
                            .jsonPath("$.userDto.surname").value(userDto.getSurname()))
                    .andExpect(MockMvcResultMatchers
                            .jsonPath("$.userDto.name").value(userDto.getName()))
                    .andExpect(MockMvcResultMatchers
                            .jsonPath("$.userDto.email").value(userDto.getEmail()))
                    .andExpect(MockMvcResultMatchers.status().isOk());

            Mockito.verify(authService).login(loginDto);
        }

    }

    @Nested
    class RegisterTests{
        private RegisterDto registerDto;

        @ParameterizedTest
        @CsvFileSource(resources = "/mock_bad_register_requests.csv", useHeadersInDisplayName = true)
        void notValidRegisterRequestTest(@AggregateWith(RegisterDtoAggregator.class) RegisterDto regDto) throws Exception {

            mockMvc.perform(post("/auth/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(regDto)))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());

        }

        @Test
        void registerFailServiceErrorTest() throws Exception {

            registerDto = new RegisterDto("testName", "testSurname", "tesssst",
                    "test@ggmail.com", "ValidPassword12345@");

            Mockito.when(authService.register(Mockito.any(RegisterDto.class)))
                    .thenThrow(RegistrationFailedException.class);

            mockMvc.perform(post("/auth/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerDto)))
                    .andExpect(MockMvcResultMatchers.status().isInternalServerError());

            Mockito.verify(authService).register(Mockito.any(RegisterDto.class));
        }

        @ParameterizedTest
        @CsvFileSource(resources = "/mock_valid_register_requests.csv", useHeadersInDisplayName = true)
        void registerSuccessTest(@AggregateWith(RegisterDtoAggregator.class) RegisterDto regDto) throws Exception {

            String message = "Success.";

            Mockito.when(authService.register(regDto)).thenReturn(message);
//            Mockito.when(authService.register(Mockito.any(RegisterDto.class))).thenReturn(message);

            mockMvc.perform(post("/auth/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(regDto)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
//                        .andExpect(MockMvcResultMatchers.content().contentType(message))
                    .andExpect(MockMvcResultMatchers.content().string(message));

            Mockito.verify(authService).register(regDto);
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

class LoginDtoAggregator implements ArgumentsAggregator {

    @Override
    public @Nullable Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) throws ArgumentsAggregationException {
        return new LoginDto(accessor.getString(0), accessor.getString(1));
    }
}
