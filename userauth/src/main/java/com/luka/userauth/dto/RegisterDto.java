package com.luka.userauth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class RegisterDto {

    @NotBlank
    private String name;
    @NotBlank
    private String surname;
    @NotBlank
    @Size(min = 3, max = 25)
    private String nick;
    @NotBlank
    @Email(regexp = "^[A-Za-z0-9.-_%+-]+@[A-Za-z0-9.-_%+-]+\\.[A-Za-z]{2,}$")
    private String email;
    @NotBlank
    @Size(min = 8, max = 25, message = "Password must be at least 8 and maximum 25 cahracter long")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit and one special character."
    )
    private String password;
//    @NotBlank
//    private String confirmPassword;

    public RegisterDto(String name, String surname, String nick, String email, String password) {
        this.name = name;
        this.surname = surname;
        this.nick = nick;
        this.email = email;
        this.password = password;
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

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        RegisterDto that = (RegisterDto) object;
        return Objects.equals(name, that.name) && Objects.equals(surname, that.surname) && Objects.equals(nick, that.nick) && Objects.equals(email, that.email) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, nick, email, password);
    }
}

