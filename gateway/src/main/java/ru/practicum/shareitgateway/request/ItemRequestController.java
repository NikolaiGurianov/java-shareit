package ru.practicum.shareitgateway.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.request.dto.IncomingItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody @Valid IncomingItemRequestDto incomingItemRequestDto) {
        log.info("Creating request {}, userId={}", incomingItemRequestDto, userId);

        return itemRequestClient.addItemRequest(userId, incomingItemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get requests by userId={}", userId);

        return itemRequestClient.getItemRequestsByAuthor(userId);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
            @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Get other requests, userId={}, from={}, size={}", userId, from, size);

        return itemRequestClient.getItemRequestsByOther(userId, from, size);
    }

    @GetMapping(value = "/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable long requestId) {
        log.info("Get request by requestId={}, userId={}", requestId, userId);

        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}