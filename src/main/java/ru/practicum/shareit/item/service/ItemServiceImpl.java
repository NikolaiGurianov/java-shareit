package ru.practicum.shareit.item.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Data
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    @Autowired
    private ItemMapper itemMapper;

    @Override
    public Item addItem(ItemDto itemDto, long ownerId) {
        return itemStorage.addItem(itemMapper.toItem(itemDto, ownerId));
    }

    @Override
    public Item updateItem(long itemId, ItemDto itemDto, long ownerId) {
        return itemStorage.updateItem(itemId, itemMapper.toItem(itemDto, ownerId), ownerId);
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return itemMapper.toItemDto(itemStorage.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getItemsByOwner(long ownerId) {
        List<Item> itemsByOwner = itemStorage.getItemsByOwner(ownerId);
        return itemsByOwner.stream().map(item -> itemMapper.toItemDto(item)).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> foundItems = itemStorage.searchItems(text);
        return foundItems.stream().map(item -> itemMapper.toItemDto(item)).collect(Collectors.toList());
    }
}
