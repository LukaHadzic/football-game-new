package com.luka.userauth.service.impl;

import com.luka.userauth.dto.LoginDto;
import com.luka.userauth.dto.LoginResponseDtoService;
import com.luka.userauth.dto.RegisterDto;
import com.luka.userauth.entity.EmailVerificationToken;
import com.luka.userauth.entity.RefreshToken;
import com.luka.userauth.exception.exceptionclasses.UserNotFoundException;
import com.luka.userauth.entity.Role;
import com.luka.userauth.entity.User;
import com.luka.userauth.exception.exceptionclasses.RegistrationFailedException;
import com.luka.userauth.exception.exceptionclasses.UserAlreadyExistsException;
import com.luka.userauth.mapper.UserMapper;
import com.luka.userauth.repository.RoleRepository;
import com.luka.userauth.repository.UserRepository;
import com.luka.userauth.security.util.JWTUtil;
import com.luka.userauth.service.AuthService;
import com.luka.userauth.service.NotificationService;
import com.luka.userauth.service.RefreshTokenService;
import com.luka.userauth.service.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.support.TransactionTemplate;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionTemplate  transactionTemplate;
    private final UserMapper userMapper;
    private final Clock clock;
    private final TokenService tokenService;
    private final NotificationService notificationService;
    private final RefreshTokenService refreshTokenService;
    private final JWTUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, TransactionTemplate transactionTemplate, UserMapper userMapper, Clock clock, TokenService tokenService, NotificationService notificationService, JWTUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.transactionTemplate = transactionTemplate;
        this.userMapper = userMapper;
        this.clock = clock;
        this.tokenService = tokenService;
        this.notificationService = notificationService;
        this.refreshTokenService = refreshTokenService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public String register(RegisterDto registerDto) {

        //Check if user already exists
        Optional<User> u1 = userRepository.findByEmail(registerDto.getEmail());
        if(u1.isPresent()) throw new UserAlreadyExistsException("Cannot finish registration - User already exists.");

        Role defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RegistrationFailedException("Server error - cannot finsih registration, default access role not found."));

        //Create entity from request data
        User user = userMapper.registerToEntity(registerDto);
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setVerified(false);
        user.setCreatedAt(LocalDateTime.now(clock));
        user.addRole(defaultRole);

        //Call TokenService to generate token
        EmailVerificationToken generatedToken = tokenService.generateToken(user);

        if (generatedToken == null){
            throw new RegistrationFailedException("Server error - cannot finish registration, cannot generate refresh token.");
        }

        //Open transaction to save User and token
        try {
            transactionTemplate.execute(status -> {
                saveTokenAndUser(user, generatedToken, defaultRole);
                return null;
            });
        }catch(Exception e) {
            e.printStackTrace();
            throw new RegistrationFailedException("Registration failed, please try again later.");
        }

        //Call NotificationService to send email to provided email address
        notificationService.sendVerificationEmail(user.getEmail(), generatedToken.getToken());

        return "Check provided email's inbox in order to verify Your identity.";
    }

    protected void saveTokenAndUser(User user, EmailVerificationToken emailVerificationToken, Role defaultRole) {
        userRepository.saveAndFlush(user);
        tokenService.saveToken(emailVerificationToken);
    }

    @Override
    public LoginResponseDtoService login(LoginDto loginDto) {
        String nickOrEmail = loginDto.getNickOrEmail();

        User user = userRepository.findByEmailOrNick(nickOrEmail)
                .orElseThrow(() -> new UserNotFoundException("Wrong login credentials."));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new UserNotFoundException("Wrong login credentials.");
        }
        
        RefreshToken refreshToken = refreshTokenService.validateOnLogin(user);
        //Nije doboro - sta ako je refresh istekao - proveriti
        //Vrv u validate(User) vratiti null ako je istekao pa ovde provera za create

        RefreshToken newRefreshToken = refreshTokenService.rotate(refreshToken.getToken());
        String token = jwtUtil.generateToken(user);

        return new LoginResponseDtoService(token, userMapper.toUserDto(user), newRefreshToken.getToken());
    }

    @Override
    public void logout(String token) {

        if (token == null || token.isEmpty()) return;
        RefreshToken dbToken = refreshTokenService.validate(token);

        if (dbToken != null) {
            refreshTokenService.revoke(token);
        }
    }
}
