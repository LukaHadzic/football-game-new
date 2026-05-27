package com.luka.userauth.dto;

import java.util.Set;

public class UserDto {

    private Long id;
    private String nick;
    private String name;
    private String surname;
    private String email;
    private Set<String> roles;

    public UserDto() {
    }

    public UserDto(Long id, String nick, String name, String surname, String email, Set<String> roles) {
        this.id = id;
        this.nick = nick;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.roles = roles;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
