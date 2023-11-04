package ru.practicum.shareitserver.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import ru.practicum.shareitserver.request.dto.ItemRequestDto;
import ru.practicum.shareitserver.request.service.ItemRequestService;
import ru.practicum.shareitserver.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestControllerTest {

    @MockBean
    ItemRequestService itemRequestService;

    private MockMvc mvc;
    ObjectMapper mapper;


    private final User user1 = new User(1L, "Test1", "test@test.ru1");
    private final User user2 = new User(2L, "Test2", "test@test.ru2");

    private ItemRequestDto requestDto1;
    private ItemRequestDto requestDto2;
    private ItemRequestDto requestDto3;


    @BeforeEach
    void beforeEach() {
        mvc = MockMvcBuilders.standaloneSetup(new ItemRequestController(itemRequestService)).build();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        requestDto1 = new ItemRequestDto(1L,
                "Item request description 1", user1, LocalDateTime.now().plusDays(2), List.of());

        requestDto2 = new ItemRequestDto(2L,
                "Item request description 2", user1, LocalDateTime.now().plusDays(4), List.of());

        requestDto3 = new ItemRequestDto(3L,
                "Item request description 3", user2, LocalDateTime.now().plusDays(6), List.of());
    }

    @Test
    void whenAddRequestIsSuccess() throws Exception {
        when(itemRequestService.createItemRequest(any(ItemRequestDto.class), anyLong())).thenReturn(requestDto1);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto1))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto1.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto1.getDescription()), String.class));

        verify(itemRequestService, times(1)).createItemRequest(requestDto1, 1L);
    }

    @Test
    void whenGetRequestsIsSuccess() throws Exception {
        when(itemRequestService.getRequestsByOwner(anyLong())).thenReturn(List.of(requestDto1, requestDto2));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(requestDto1, requestDto2))));

        verify(itemRequestService, times(1)).getRequestsByOwner(1L);
    }

    @Test
    void whenGetAllRequestsIsSuccess() throws Exception {
        when(itemRequestService.getAllRequests(anyLong(), anyInt(), anyInt())).thenReturn(List.of(requestDto1, requestDto2, requestDto3));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(requestDto1, requestDto2, requestDto3))));

        verify(itemRequestService, times(1)).getAllRequests(1L, 0, 20);
    }

    @Test
    void whenGetRequestByIdIsSuccess() throws Exception {
        when(itemRequestService.getRequestById(anyLong(), anyLong())).thenReturn(requestDto2);

        mvc.perform(get("/requests/{requestId}", 2L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(requestDto2.getDescription()), String.class));

        verify(itemRequestService, times(1)).getRequestById(1L, 2L);
    }
}