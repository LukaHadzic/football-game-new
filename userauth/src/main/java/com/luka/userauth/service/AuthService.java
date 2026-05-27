package com.luka.userauth.service;

import com.luka.userauth.dto.LoginDto;
import com.luka.userauth.dto.LoginResponseDto;

import com.luka.userauth.dto.RegisterDto;

public interface AuthService {

    public String register(RegisterDto registerDto);

    public LoginResponseDto login(LoginDto loginDto);

}
