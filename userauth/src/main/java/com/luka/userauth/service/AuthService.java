package com.luka.userauth.service;

import com.luka.userauth.dto.RegisterDto;

public interface AuthService {

    public String register(RegisterDto registerDto);

}
