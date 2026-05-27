package com.luka.userauth.mapper;

import com.luka.userauth.dto.UserDto;
import com.luka.userauth.entity.Role;
import com.luka.userauth.entity.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

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
