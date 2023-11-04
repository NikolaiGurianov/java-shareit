package ru.practicum.shareitserver.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareitserver.exception.NotFoundException;
import ru.practicum.shareitserver.user.dto.UserDto;
import ru.practicum.shareitserver.user.dto.UserMapper;
import ru.practicum.shareitserver.user.model.User;
import ru.practicum.shareitserver.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private final UserRepository userRepository;

    private User user1;
    private User user2;
    private UserDto userDto1;
    private UserDto userDto2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        UserMapper userMapper = new UserMapper();
        userService = new UserServiceImpl(userRepository, userMapper);

        user1 = new User();
        user1.setId(1L);
        user1.setName("test name");
        user1.setEmail("test@test.ru");

        user2 = new User();
        user2.setId(2L);
        user2.setName("test name2");
        user2.setEmail("test2@test.ru");

        userDto1 = userMapper.toUserDto(user1);
        userDto2 = userMapper.toUserDto(user2);
    }

    @Test
    void whenCreateUserIsSuccess() {
        when(userRepository.save(any())).thenReturn(user1);

        UserDto actual = userService.createUser(userDto1);

        assertEquals(actual, userDto1);
    }

    @Test
    void whenUpdateUserIsSuccess() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(userRepository.save(any())).thenReturn(user2);

        UserDto actual = userService.updateUser(userDto2, user1.getId());

        assertEquals(actual, userDto2);
    }

    @Test
    void whenUpdateUserByInvalidUserIdIsNotSuccess() {
        String expectedMessage = "Пользователь не найден с ID={}";

        assertThatThrownBy(() -> userService.updateUser(userDto2, 999L))
                .isInstanceOf(NotFoundException.class)
                .message()
                .isEqualTo(expectedMessage);
    }

    @Test
    void whenGetUsersIsSuccess() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDto> actual = userService.getUsers();

        assertEquals(actual, List.of(userDto1, userDto2));
    }

    @Test
    void whenGetUserByIdIsSuccess() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        UserDto actual = userService.getUserById(user1.getId());

        assertEquals(actual, userDto1);
    }

    @Test
    void whenGetUserByIdIsNotSuccess() {
        String expectedMessage = "Пользователь не найден с ID={}";

        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(NotFoundException.class)
                .message()
                .isEqualTo(expectedMessage);
    }
}
