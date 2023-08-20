package ru.practicum.shareit.user.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ErrorException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Data
@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getUsers() {
        return userStorage.getUsers().stream().map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public User createUser(UserDto userDto) {
        if (userDto != null) return userStorage.createUser(userMapper.toUser(userDto));
        throw new ErrorException("Нет данных для создания пользователя");
    }

    @Override
    public User updateUser(UserDto userDto, long userId) {
        if (userDto != null) return userStorage.updateUser(userMapper.toUser(userDto), userId);
        throw new ErrorException("Нет данных для обновления пользователя");
    }

    @Override
    public void deleteUserById(long userId) {
        userStorage.deleteUserById(userId);
    }

    @Override
    public UserDto getUserById(long userId) {
        return userMapper.toUserDto(userStorage.getUserById(userId));
    }
}
