package com.luka.userauth.controller;

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
