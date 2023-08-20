package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers();

    User createUser(UserDto userDto);

    User updateUser(UserDto user, long userId);

    void deleteUserById(long userId);

    UserDto getUserById(long userId);
}
