package com.luka.userauth.dto;

public class LoginResponseDto {

    private UserDto userDto;

    public LoginResponseDto(UserDto userDto) {
        this.userDto = userDto;
    }

    public UserDto getUserDto() {
        return userDto;
    }

    public void setUserDto(UserDto userDto) {
        this.userDto = userDto;
    }
}
