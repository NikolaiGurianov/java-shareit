package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public User createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос на создание нового пользователя");
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable long userId,
                           @RequestBody UserDto userDto) {
        log.info("Получен запрос на обновление пользователя с ID={}", userId);
        return userService.updateUser(userDto, userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        log.info("Получен запрос на выдачу пользователя с ID={}", userId);
        return userService.getUserById(userId);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("Получен запрос на выдачу всех пользователей");
        return userService.getUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("Получен запрос на удаление пользователя с ID={}", userId);
        userService.deleteUserById(userId);
    }
}
