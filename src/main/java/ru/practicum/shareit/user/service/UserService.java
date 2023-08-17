package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<User> getUsers();

    User createUser(User user);

    User updateUser(User user, long userId);

    void deleteUserById(long userId);

    User getUserById(long userId);
}
