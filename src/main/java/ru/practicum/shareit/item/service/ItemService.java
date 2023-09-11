package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemLastNextDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item addItem(ItemDto itemDto, Long ownerId);

    ItemDto updateItem(long itemId, ItemDto itemDto, Long ownerId);

    ItemLastNextDto getItemById(long itemId, long usrId);

    List<ItemLastNextDto> getItemsByOwner(long ownerId);

    List<ItemDto> searchItems(String text);

    CommentDto addComment(Long userId, CommentDto commentDto, Long itemId);

}
