package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface UserService {
    List<UserDto> getUsers();

    @Transactional
    User createUser(UserDto userDto);

    User updateUser(UserDto user, Long userId);

    @Transactional
    void deleteUserById(long userId);

    @Transactional
    UserDto getUserById(Long userId);
}
