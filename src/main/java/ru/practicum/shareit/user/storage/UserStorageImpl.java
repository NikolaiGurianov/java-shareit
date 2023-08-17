package ru.practicum.shareit.user.storage;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
@Component
public class UserStorageImpl implements UserStorage {
    private final Map<Long, User> users;
    private long generateId = 1L;

    @Override
    public List<User> getUsers() {
        log.info("Выдан список всех пользователей");
        return new ArrayList<>(users.values());
    }

    @Override
    public User createUser(User user) {
        if (existsByEmail(user.getEmail())) throw new ErrorException("Пользователь с таким email уже существует");
        user.setId(generateId++);
        users.put(user.getId(), user);
        log.info("Пользователь успешно создан");
        return user;
    }

    public boolean existsByEmail(String email) {
        return users.values().stream().anyMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public User updateUser(User updatedUser, long userId) {
        User user = getUserById(userId);
        if (user == null) throw new NotFoundException("Пользователь не найден с ID={}", userId);
        if (!user.getEmail().equals(updatedUser.getEmail()) && existsByEmail(updatedUser.getEmail()))
            throw new ErrorException("Пользователь с таким email уже существует");
        if (updatedUser.getName() != null && !updatedUser.getName().isBlank()) user.setName(updatedUser.getName());
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().isBlank()) user.setEmail(updatedUser.getEmail());
        return user;
    }


    @Override
    public void deleteUserById(long userId) {
        if (users.containsKey(userId)) {
            users.remove(userId);
            log.info("Пользователь с ID = {} удален.", userId);
        } else {
            log.info("Пользователь с id = {} не найден.", userId);
            throw new NotFoundException("Пользователь с id = {} не найден.", userId);
        }
    }

    @Override
    public User getUserById(long userId) {
        try {
            log.info("Выдан пользователь с ID={}", userId);
            return users.get(userId);
        } catch (Exception e) {
            log.error("Ошибка при попытке получить пользователя с ID={}: {}", userId, e.getMessage());
            throw new NotFoundException("пользователь с таким ID  не найдена");
        }
    }
}
