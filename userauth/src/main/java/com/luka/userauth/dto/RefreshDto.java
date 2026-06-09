package com.luka.userauth.dto;

public class RefreshDto {

    private String token;

    public RefreshDto() {
    }

    public RefreshDto(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
