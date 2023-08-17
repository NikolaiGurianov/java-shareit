package ru.practicum.shareit.user.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ErrorException;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Slf4j
@Data
@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    @Override
    public User createUser(User user) {
        if (user != null) return userStorage.createUser(user);
        throw new ErrorException("Нет данных для создания пользователя");
    }

    @Override
    public User updateUser(User user, long userId) {
        if (user != null) return userStorage.updateUser(user, userId);
        throw new ErrorException("Нет данных для обновления пользователя");
    }


    @Override
    public void deleteUserById(long userId) {
        userStorage.deleteUserById(userId);
    }

    @Override
    public User getUserById(long userId) {
        return userStorage.getUserById(userId);
    }
}
