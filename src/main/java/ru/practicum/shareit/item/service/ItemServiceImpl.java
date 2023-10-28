package ru.practicum.shareit.item.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.exception.ErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemLastNextDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.comment.dto.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Data
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Override
    public ItemDto addItem(ItemDto itemDto, Long ownerId) {
        User owner = userRepository.findById(ownerId).orElseThrow(()
                -> new NotFoundException("Пользователь не найден с ID={}", ownerId));
        Item item = itemRepository.save(itemMapper.toItem(itemDto, owner));
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(long itemId, ItemDto itemDto, Long ownerId) {
        Item item = itemRepository.findById(itemId).orElseThrow(()
                -> new NotFoundException("Вещь не найдена с ID={}", itemId));
        if (!ownerId.equals(item.getOwnerId()))
            throw new ErrorException("Пользователь не является владельцем данной вещи");
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null
                && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemLastNextDto getItemById(long itemId, long userId) {
        LocalDateTime now = LocalDateTime.now();
        Item item = itemRepository.findById(itemId).orElseThrow(()
                -> new NotFoundException("Вещь не найдена с ID={}", itemId));
        Booking last = null;
        Booking next = null;
        if (item.getOwnerId().equals(userId)) {
            last = bookingRepository.findLast(itemId, now);
            next = bookingRepository.findNext(itemId, now);
        }
        return itemMapper.toItemLastNextDto(item, last, next, commentRepository.findByItem_Id(itemId));
    }

    @Override
    public List<ItemLastNextDto> getItemsByOwner(long ownerId, Integer from, Integer size) {
        User owner = userRepository.findById(ownerId).orElseThrow(()
                -> new NotFoundException("Пользователь не найден с ID={}", ownerId));
        LocalDateTime now = LocalDateTime.now();
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Item> items = itemRepository.findAllByOwnerId(ownerId, pageRequest);
        List<ItemLastNextDto> list = new ArrayList<>();
        Booking last;
        Booking next;
        for (Item item : items) {
            if (item.getOwnerId().equals(ownerId)) {
                last = bookingRepository.findLast(item.getId(), now);
                next = bookingRepository.findNext(item.getId(), now);
                list.add(itemMapper.toItemLastNextDto(item, last, next, commentRepository.findByItem_Id(item.getId())));
            }
        }
        return list;
    }

    @Override
    public List<ItemDto> searchItems(String text, Integer from, Integer size) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Item> findItems = itemRepository
                .findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text, pageRequest);
        return findItems.stream().filter(Item::getAvailable).map(itemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long userId, CommentDto commentDto, Long itemId) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("Пользователь не найден с ID={}", userId));
        Item item = itemRepository.findById(itemId).orElseThrow(()
                -> new NotFoundException("Вещь не найдена с ID={}", itemId));
        if (!bookingRepository.existsByBooker_IdAndEndBeforeAndStatus(userId, LocalDateTime.now(), Status.APPROVED)) {
            throw new ValidException("Комментарий не может быть создан");
        }
        return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(commentDto, item, user)));
    }
}
