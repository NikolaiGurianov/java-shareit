package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ErrorException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @Valid @RequestBody ItemRequestDto requestDto) {
        log.info("Получен запрос на создание запроса вещи от пользователя ID={}", userId);
        return itemRequestService.createItemRequest(requestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение списка запросов от пользователя ID={}", userId);

        return itemRequestService.getRequestsByOwner(userId);

    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "20") @Positive Integer size) {
        log.info("Получен запрос на получение списка запросов с параметрами, созданные другими пользователями");
        if (from >= 0 && size > 0) {
            return itemRequestService.getAllRequests(userId, from, size);
        }
        throw new ErrorException("Заданы неправильные параметры");
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long requestId) {
        log.info("Получен запрос на получение запросов с параметрами, созданные другими пользователями");

        return itemRequestService.getRequestById(userId, requestId);
    }

}
