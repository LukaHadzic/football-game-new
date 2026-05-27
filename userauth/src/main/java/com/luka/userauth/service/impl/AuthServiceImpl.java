package com.luka.userauth.service.impl;

import com.luka.userauth.dto.LoginDto;
import com.luka.userauth.dto.LoginResponseDto;
import com.luka.userauth.entity.User;
import com.luka.userauth.exception.exceptionclasses.UserNotFoundException;
import com.luka.userauth.mapper.UserMapper;
import com.luka.userauth.repository.UserRepository;
import com.luka.userauth.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public LoginResponseDto login(LoginDto loginDto) {
        String nickOrEmail = loginDto.getNickOrEmail();

        User user = userRepository.findByEmailOrNick(nickOrEmail)
                .orElseThrow(() -> new UserNotFoundException("Wrong login credentials."));

        if(!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())){
            throw new UserNotFoundException("Wrong login credentials.");
        }

        return new LoginResponseDto(userMapper.toUserDto(user));
    }
}
