package ru.practicum.shareitServer.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareitServer.user.model.User;

@Component
public class UserMapper {
    public User toUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }
}
