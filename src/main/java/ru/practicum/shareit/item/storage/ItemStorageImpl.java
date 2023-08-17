package ru.practicum.shareit.item.storage;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
@Component
public class ItemStorageImpl implements ItemStorage {
    private final Map<Long, Item> items;
    private final UserStorage userStorage;
    private long generateId = 1L;

    @Override
    public List<Item> getItems() {
        log.info("Выдан список всех вещей");
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> getItemsByOwner(long userId) {
        List<Item> userItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwnerId().equals(userId)) {
                userItems.add(item);
            }
        }
        log.info("Выдан весь список вещей пользователя с ID={}", userId);
        return userItems;
    }

    @Override
    public Item getItemById(long itemId) {
        if (items.containsKey(itemId)) {
            log.info("Выдана вещь с ID={}", itemId);
            return items.get(itemId);
        }
        log.error("Ошибка при попытке получить вещь с ID={}", itemId);
        throw new NotFoundException("Вещь с таким ID  не найдена");
    }

    @Override
    public Item addItem(Item item) {
        long userId = item.getOwnerId();
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь c ID {} не найден", userId);
        }
        item.setId(generateId++);
        items.put(item.getId(), item);
        log.info("Создана вещь:{}", item);
        return item;
    }

    @Override
    public Item updateItem(long itemId, Item updatedItem, long userId) {
        User user = userStorage.getUserById(userId);
        if (user == null || !getItemById(itemId).getOwnerId().equals(userId)) {
            log.error("Пользователь для обновления вещи не найден");
            throw new NotFoundException("Пользователь c ID {} не найден", userId);
        }
        if (items.containsKey(itemId)) {
            Item item = items.get(itemId);
            if (updatedItem.getName() != null && !updatedItem.getName().isBlank()) {
                item.setName(updatedItem.getName());
            }
            if (updatedItem.getDescription() != null
                    && !updatedItem.getDescription().isBlank()) {
                item.setDescription(updatedItem.getDescription());
            }
            if (updatedItem.getAvailable() != null) {
                item.setAvailable(updatedItem.getAvailable());
            }
            log.info("Вещь успешно обновлена");
            return item;
        } else {
            log.error("Вещь для обновления не найдена");
            throw new NotFoundException("Вещь не найдена");
        }
    }

    @Override
    public void deleteItemById(long id) {
        if (items.containsKey(id)) {
            items.remove(id);
            log.info("Вещь с ID = {} удалена.", id);
        } else {
            log.info("Вещь с id = {} не найдена.", id);
            throw new NotFoundException("Вещь с id = {} не найдена.", id);
        }
    }

    @Override
    public List<Item> searchItems(String keyword) {
        List<Item> result = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getAvailable()
                    && (item.getName().toLowerCase().contains(keyword.toLowerCase())
                    || item.getDescription().toLowerCase().contains(keyword.toLowerCase()))) {
                result.add(item);
            }
        }
        return result;
    }
}
