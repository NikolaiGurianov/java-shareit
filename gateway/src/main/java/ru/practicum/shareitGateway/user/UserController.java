package ru.practicum.shareitGateway.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitGateway.user.dto.IncomingUserDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody IncomingUserDto incomingUserDto) {
        log.info("Create user {}", incomingUserDto);

        return userClient.addUser(incomingUserDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody IncomingUserDto incomingUserDto,
                                             @PathVariable long userId) {
        log.info("Update user {}, userId={}", incomingUserDto, userId);

        return userClient.updateUserById(userId, incomingUserDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        log.info("Get user with ID={}", userId);

        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Get all users");

        return userClient.getAllUsers();
    }

    @DeleteMapping(value = "/{userId}")
    public ResponseEntity<Object> deleteUserBy(@PathVariable long userId) {
        log.info("Delete user with ID={}", userId);

        return userClient.deleteUserById(userId);
    }
}
