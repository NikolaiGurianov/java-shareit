package ru.practicum.shareitServer.user.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareitServer.exception.ErrorException;
import ru.practicum.shareitServer.exception.NotFoundException;
import ru.practicum.shareitServer.user.dto.UserDto;
import ru.practicum.shareitServer.user.dto.UserMapper;
import ru.practicum.shareitServer.user.repository.UserRepository;
import ru.practicum.shareitServer.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Data
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getUsers() {
        List<User> userList = userRepository.findAll();
        return userList.stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto == null) throw new ErrorException("Нет данных для создания пользователя");
        return userMapper.toUserDto(userRepository.save(userMapper.toUser(userDto)));
    }


    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("Пользователь не найден с ID={}", userId));
        if (userDto.getName() != null && !userDto.getName().isBlank()) user.setName(userDto.getName());
        if (userDto.getEmail() != null && !user.getEmail().equals(userDto.getEmail()))
            user.setEmail(userDto.getEmail());
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUserById(long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public UserDto getUserById(Long userId) {
        return userMapper.toUserDto(userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("Пользователь не найден с ID={}", userId)));
    }
}
