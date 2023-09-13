package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemLastNextDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public Item addItem(@Valid @RequestBody ItemDto itemDto,
                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на добавление новой вещи вещи для пользователя с ID = {}", userId);
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId,
                              @RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на обновление вещи с ID = {}, для пользователя с ID = {}", itemId, userId);
        return itemService.updateItem(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemLastNextDto getItemById(@PathVariable long itemId,
                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на выдачу вещи с ID = {}", itemId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemLastNextDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос на выдачу вещей пользователя с ID = {}", userId);
        return itemService.getItemsByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Получен запрос на поиск вещей по ключевому слову {}", text);
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Valid @RequestBody CommentDto commentDto,
                                 @PathVariable Long itemId) {
        log.info("Получен запрос на добавление комментария о вещи с ID={} то пользователя с ID={}", itemId, userId);
        return itemService.addComment(userId, commentDto, itemId);
    }
}
