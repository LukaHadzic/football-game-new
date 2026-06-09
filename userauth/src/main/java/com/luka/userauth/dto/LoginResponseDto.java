package com.luka.userauth.dto;

import com.luka.userauth.entity.RefreshToken;

public class LoginResponseDto {

    private UserDto userDto;
    private String refreshToken;

    public LoginResponseDto(UserDto userDto, String refreshToken) {
        this.userDto = userDto;
        this.refreshToken = refreshToken;
    }

    public UserDto getUserDto() {
        return userDto;
    }

    public void setUserDto(UserDto userDto) {
        this.userDto = userDto;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
