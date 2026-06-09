package com.luka.userauth.dto;

public class LoginResponseDtoService {

    private String accessToken;
    private UserDto userDto;
    private String refreshToken;

    public LoginResponseDtoService(String accessToken, UserDto userDto, String refreshToken) {
        this.userDto = userDto;
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
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

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
