package com.luka.userauth.mapper;

import com.luka.userauth.dto.UserDto;
import com.luka.userauth.entity.Role;
import com.luka.userauth.entity.User;
import com.luka.userauth.dto.RegisterDto;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

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

    public UserDto toUserDto(User user){
        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setNick(user.getNick());
        userDto.setName(user.getName());
        userDto.setSurname(user.getSurname());
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));

        return userDto;
    }
}
