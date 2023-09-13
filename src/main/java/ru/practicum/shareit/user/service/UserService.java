package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface UserService {
    List<UserDto> getUsers();

    User createUser(UserDto userDto);

    User updateUser(UserDto user, Long userId);

    void deleteUserById(long userId);

    UserDto getUserById(Long userId);
}
