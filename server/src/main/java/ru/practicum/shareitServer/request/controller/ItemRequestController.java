package ru.practicum.shareitServer.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitServer.request.dto.ItemRequestDto;
import ru.practicum.shareitServer.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @RequestBody ItemRequestDto requestDto) {
        log.info("Получен запрос на создание запроса вещи от пользователя ID={}", userId);
        return itemRequestService.createItemRequest(requestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос на получение списка запросов от пользователя ID={}", userId);

        return itemRequestService.getRequestsByOwner(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Получен запрос на получение списка запросов с параметрами, созданные другими пользователями");
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable long requestId) {
        log.info("Получен запрос на получение запросов с параметрами, созданные другими пользователями");

        return itemRequestService.getRequestById(userId, requestId);
    }

}
