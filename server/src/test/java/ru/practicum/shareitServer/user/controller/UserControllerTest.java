package ru.practicum.shareitServer.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareitServer.user.controller.UserController;
import ru.practicum.shareitServer.user.dto.UserDto;
import ru.practicum.shareitServer.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTest {
    @MockBean
    UserService userService;
    private MockMvc mvc;
    private ObjectMapper mapper;


    private UserDto userDto1;
    private UserDto userDto2;

    @BeforeEach
    void beforeEach() {
        mvc = MockMvcBuilders.standaloneSetup(new UserController(userService)).build();
        mapper = new ObjectMapper();

        userDto1 = new UserDto(1L, "Test1", "1test@test.ru");
        userDto2 = new UserDto(2L, "Test2", "2test@test.ru");
    }

    @Test
    void whenAddUserIsSuccess() throws Exception {
        when(userService.createUser(any(UserDto.class))).thenReturn(userDto1);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto1.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail()), String.class));

        verify(userService, times(1)).createUser(userDto1);
    }

    @Test
    void whenUpdateUserIsSuccess() throws Exception {
        when(userService.updateUser(any(UserDto.class), anyLong())).thenReturn(userDto1);

        mvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto1.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail()), String.class));

        verify(userService, times(1)).updateUser(userDto1, 1L);
    }

    @Test
    void whenGetUsersIsSuccess() throws Exception {
        when(userService.getUsers()).thenReturn(List.of(userDto1, userDto2));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(userDto1, userDto2))));

        verify(userService, times(1)).getUsers();
    }

    @Test
    void whenGetUserIsSuccess() throws Exception {
        when(userService.getUserById(1L)).thenReturn(userDto1);

        mvc.perform(get("/users/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto1.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail()), String.class));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void deleteUser() throws Exception {
        mvc.perform(delete("/users/{userId}", 1L))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUserById(1L);
    }
}