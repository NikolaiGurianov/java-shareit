package ru.practicum.shareitServer.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitServer.item.comment.dto.CommentDto;
import ru.practicum.shareitServer.item.dto.ItemDto;
import ru.practicum.shareitServer.item.dto.ItemLastNextDto;
import ru.practicum.shareitServer.item.service.ItemService;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestBody ItemDto itemDto,
                           @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос на добавление новой вещи вещи для пользователя с ID = {}", userId);
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId,
                              @RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос на обновление вещи с ID = {}, для пользователя с ID = {}", itemId, userId);
        return itemService.updateItem(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemLastNextDto getItemById(@PathVariable long itemId,
                                       @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос на выдачу вещи с ID = {}", itemId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemLastNextDto> getItemsByOwner(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Получен запрос на выдачу вещей пользователя с ID = {}", userId);
        return itemService.getItemsByOwner(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(
            @RequestParam String text,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "20") Integer size) {
        log.info("Получен запрос на поиск вещей по ключевому слову {}", text);
        return itemService.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody CommentDto commentDto,
                                 @PathVariable Long itemId) {
        log.info("Получен запрос на добавление комментария о вещи с ID={} то пользователя с ID={}", itemId, userId);
        return itemService.addComment(userId, commentDto, itemId);
    }
}
