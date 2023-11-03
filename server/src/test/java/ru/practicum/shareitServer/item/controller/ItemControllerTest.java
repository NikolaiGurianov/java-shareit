package ru.practicum.shareitServer.item.controller;

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
import ru.practicum.shareitServer.item.comment.dto.CommentDto;
import ru.practicum.shareitServer.item.controller.ItemController;
import ru.practicum.shareitServer.item.dto.ItemDto;
import ru.practicum.shareitServer.item.dto.ItemLastNextDto;
import ru.practicum.shareitServer.item.dto.ItemMapper;
import ru.practicum.shareitServer.item.model.Item;
import ru.practicum.shareitServer.item.service.ItemService;
import ru.practicum.shareitServer.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemControllerTest {
    @MockBean
    ItemService itemService;

    private MockMvc mvc;
    private ObjectMapper mapper;

    private User user;
    private Item item1;
    private Item item2;
    private CommentDto commentDto;
    private ItemDto itemDto1;
    private ItemDto itemDto2;
    private ItemLastNextDto itemLN;
    private final ItemMapper itemMapper = new ItemMapper();


    @BeforeEach
    void beforeEach() {
        mvc = MockMvcBuilders.standaloneSetup(new ItemController(itemService)).build();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        user = new User(1L, "Test1", "1test@test.ru");
        item1 = new Item(1L, "Test Item1", "Description Of Item Test1", true, user, 1L);
        item2 = new Item(2L, "Test Item2", "Description Of Item Test2", true, user, 1L);


        itemDto1 = itemMapper.toItemDto(item1);
        itemDto2 = itemMapper.toItemDto(item2);

        itemLN = itemMapper.toItemLastNextDto(item1, null, null, List.of());

        commentDto = new CommentDto(1L, "Comment test", user.getName(), LocalDateTime.now());

    }

    @Test
    void addItemTest() throws Exception {
        when(itemService.addItem(any(ItemDto.class), anyLong())).thenReturn(itemDto1);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto1))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto1.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto1.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto1.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(itemDto1.getRequestId()), Long.class));

        verify(itemService, times(1)).addItem(itemDto1, 1L);
    }

    @Test
    void updateItemTest() throws Exception {
        when(itemService.updateItem(anyLong(), any(ItemDto.class), anyLong())).thenReturn(itemDto2);

        mvc.perform(patch("/items/{itemId}", 1L)
                        .content(mapper.writeValueAsString(itemDto2))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto2.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto2.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto2.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto2.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(itemDto2.getRequestId()), Long.class));

        verify(itemService, times(1)).updateItem(1, itemDto2, 1L);
    }

    @Test
    void getItemByIdTest() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemLN);

        mvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item1.getName()), String.class))
                .andExpect(jsonPath("$.description", is(item1.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(item1.getAvailable()), Boolean.class));

        verify(itemService, times(1)).getItemById(1L, 1L);
    }

    @Test
    void getItemsByOwnerTest() throws Exception {
        when(itemService.getItemsByOwner(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemLN));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemLN))));

        verify(itemService, times(1)).getItemsByOwner(1L, 0, 20);
    }

    @Test
    void searchItemsTest() throws Exception {
        when(itemService.searchItems(anyString(), anyInt(), anyInt())).thenReturn(List.of(itemDto1, itemDto2));

        mvc.perform(get("/items/search")
                        .param("text", "test"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto1, itemDto2))));

        verify(itemService, times(1)).searchItems("test", 0, 20);
    }

    @Test
    void addCommentTest() throws Exception {
        when(itemService.addComment(anyLong(), any(CommentDto.class), anyLong())).thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentDto)));

        verify(itemService, times(1)).addComment(1L, commentDto, 1L);
    }


}
