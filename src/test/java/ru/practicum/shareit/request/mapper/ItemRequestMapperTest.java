package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemRequestMapperTest {
    private final ItemRequestMapper itemRequestMapper = new ItemRequestMapper();

    @Test
    public void whenToItemRequestIsSuccess() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        User user = new User();

        itemRequestDto.setDescription("Test Request Description");

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto, user);

        assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription());
        assertEquals(user, itemRequest.getRequester());
    }

    @Test
    public void whenToItemRequestDtoIsSuccess() {
        User user = new User();

        ItemRequest itemRequest = new ItemRequest(1L, "dsecription", user, LocalDateTime.now());

        List<ItemDto> itemList = new ArrayList<>();

        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest, itemList, user);

        assertEquals(itemRequest.getId(), itemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());
        assertEquals(user, itemRequestDto.getRequester());
    }

}
