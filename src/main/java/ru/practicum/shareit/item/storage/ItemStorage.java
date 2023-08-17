package ru.practicum.shareit.item.storage;


import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    List<Item> getItems();

    List<Item> getItemsByOwner(long userId);

    Item getItemById(long id);

    Item addItem(Item item);

    Item updateItem(long itemId, Item item, long userId);

    void deleteItemById(long id);

    List<Item> searchItems(String keyword);
}
