package com.luka.userauth.service;

import com.luka.userauth.dto.LoginDto;
import com.luka.userauth.dto.LoginResponseDto;

public interface AuthService {

    public LoginResponseDto login(LoginDto loginDto);
}
