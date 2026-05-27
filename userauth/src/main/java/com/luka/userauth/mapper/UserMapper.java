package com.luka.userauth.mapper;

import com.luka.userauth.dto.RegisterDto;
import com.luka.userauth.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User registerToEntity(RegisterDto registerDto) {
        User user = new User();

        user.setName(registerDto.getName());
        user.setSurname(registerDto.getSurname());
        user.setNick(registerDto.getNick());
        user.setEmail(registerDto.getEmail());

        return user;
    }
}
