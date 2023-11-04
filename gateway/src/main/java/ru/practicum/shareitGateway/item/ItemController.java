package ru.practicum.shareitGateway.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitGateway.item.dto.IncomingCommentDto;
import ru.practicum.shareitGateway.item.dto.IncomingItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestBody @Valid IncomingItemDto incomingItemDto) {
        log.info("Creating item {}, userId={}", incomingItemDto, userId);

        return itemClient.addItem(userId, incomingItemDto);
    }

    @PatchMapping(value = "/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody IncomingItemDto incomingItemDto,
                                             @PathVariable long itemId) {
        log.info("Updating item {}, itemId={}, userId={}", incomingItemDto, itemId, userId);

        return itemClient.updateItem(incomingItemDto, itemId, userId);
    }

    @GetMapping(value = "/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PathVariable long itemId) {
        log.info("Get item, itemId={}, userId={}", itemId, userId);

        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Get items by userId={}, from={}, size={}", userId, from, size);

        return itemClient.getItemsByUser(userId, from, size);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<Object> searchItems(@NotBlank @RequestParam String text,
                                              @RequestHeader("X-Sharer-User-Id") long userId,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Get items by text {}, from={}, size={}, userId={}", text, from, size, userId);

        return itemClient.searchItemsByText(userId, text, from, size);
    }

    @PostMapping(value = "/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody @Valid IncomingCommentDto incomingCommentDto,
                                             @PathVariable long itemId) {
        log.info("Creating comment {}, itemId={}, userId={}", incomingCommentDto.getText(), itemId, userId);

        return itemClient.addComment(userId, itemId, incomingCommentDto);
    }
}