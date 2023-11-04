package ru.practicum.shareitserver.user.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareitserver.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {
    private final UserMapper userMapper = new UserMapper();

    @Test
    public void whenToUserIsSuccess() {
        UserDto userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");

        User user = userMapper.toUser(userDto);

        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    public void whenToUserDtoIsSuccess() {
        User user = new User(1L, "Test", "test@test");

        UserDto userDto = userMapper.toUserDto(user);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }
}
