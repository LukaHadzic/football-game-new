package com.luka.userauth.service.impl;

import com.luka.userauth.dto.RegisterDto;
import com.luka.userauth.entity.Role;
import com.luka.userauth.entity.User;
import com.luka.userauth.exception.exceptionclasses.RegistrationFailedException;
import com.luka.userauth.exception.exceptionclasses.UserAlreadyExistsException;
import com.luka.userauth.mapper.UserMapper;
import com.luka.userauth.repository.RoleRepository;
import com.luka.userauth.repository.UserRepository;
import com.luka.userauth.service.AuthService;
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

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, TransactionTemplate transactionTemplate, UserMapper userMapper, Clock clock) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.transactionTemplate = transactionTemplate;
        this.userMapper = userMapper;
        this.clock = clock;
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

        //Open transaction to save User and token
        try {
            transactionTemplate.execute(status -> {
                userRepository.save(user);
                return null;
            });
        }catch(Exception e) {
            e.printStackTrace();
            throw new RegistrationFailedException("Registration failed, please try again later.");
        }

        return "Successfully registered.";
    }
}
