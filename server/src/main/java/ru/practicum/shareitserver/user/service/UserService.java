package ru.practicum.shareitserver.user.service;

import ru.practicum.shareitserver.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers();

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto user, Long userId);

    void deleteUserById(long userId);

    UserDto getUserById(Long userId);
}
