package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item addItem(ItemDto itemDto, long ownerId);

    Item updateItem(long itemId, ItemDto itemDto, long ownerId);

    ItemDto getItemById(long itemId);

    List<ItemDto> getItemsByOwner(long ownerId);

    List<ItemDto> searchItems(String text);

}
