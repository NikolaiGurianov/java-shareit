package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemLastNextDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto itemDto, Long ownerId);

    ItemDto updateItem(long itemId, ItemDto itemDto, Long ownerId);

    ItemLastNextDto getItemById(long itemId, long usrId);

    List<ItemLastNextDto> getItemsByOwner(long ownerId, Integer from, Integer size);

    List<ItemDto> searchItems(String text, Integer from, Integer size);

    CommentDto addComment(Long userId, CommentDto commentDto, Long itemId);

}