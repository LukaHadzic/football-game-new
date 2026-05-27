package com.luka.userauth.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

public class LoginDto {

    @NotBlank
    private String nickOrEmail;
    @NotBlank
    private String password;

    public LoginDto() {
    }

    public LoginDto(String nickOrEmail, String password) {
        this.nickOrEmail = nickOrEmail;
        this.password = password;
    }

    public String getNickOrEmail() {
        return nickOrEmail;
    }

    public void setNickOrEmail(String nickOrEmail) {
        this.nickOrEmail = nickOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        LoginDto loginDto = (LoginDto) object;
        return Objects.equals(nickOrEmail, loginDto.nickOrEmail) && Objects.equals(password, loginDto.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickOrEmail, password);
    }
}
