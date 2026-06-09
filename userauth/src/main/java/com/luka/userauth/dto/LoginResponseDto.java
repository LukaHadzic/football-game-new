package com.luka.userauth.dto;

public class LoginResponseDto {

    private String accessToken;
    private UserDto userDto;

    public LoginResponseDto(String accessToken, UserDto userDto) {

        this.userDto = userDto;
        this.accessToken = accessToken;
    }

    public UserDto getUserDto() {
        return userDto;
    }

    public void setUserDto(UserDto userDto) {
        this.userDto = userDto;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
