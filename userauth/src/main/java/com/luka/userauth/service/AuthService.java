package com.luka.userauth.service;

import com.luka.userauth.dto.LoginDto;
import com.luka.userauth.dto.LoginResponseDtoService;

import com.luka.userauth.dto.RegisterDto;

public interface AuthService {

    public String register(RegisterDto registerDto);

    public LoginResponseDtoService login(LoginDto loginDto);

}
