package ru.practicum.shareit.request.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Data
@Service
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден с ID={}", userId));

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto, user);

        return itemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest), List.of(), user);
    }

    @Override
    public List<ItemRequestDto> getRequestsByOwner(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден с ID={}", userId));

        List<ItemRequest> requestsList = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);

        List<Long> requestIds = requestsList.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        Map<Long, List<Item>> itemsByRequests = new HashMap<>();
        if (!requestIds.isEmpty()) {
            itemsByRequests = itemRepository.findByRequestIdIn(requestIds, Sort.by(Sort.Direction.DESC, "id"))
                    .stream()
                    .collect(Collectors.groupingBy(Item::getRequestId));
        }

        List<ItemRequestDto> result = new ArrayList<>();
        List<ItemDto> itemsList = new ArrayList<>();
        for (ItemRequest itemRequest : requestsList) {
            if (!itemsByRequests.isEmpty()) {
                itemsList = itemsByRequests.get(itemRequest.getId()).stream().map(itemMapper::toItemDto).collect(toList());
            }
            ItemRequestDto dto = itemRequestMapper.toItemRequestDto(itemRequest, itemsList, user);
            result.add(dto);
        }

        return result;
    }


    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден с ID={}", userId));

        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<ItemRequest> requestList =
                itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId, pageRequest);

        List<Long> requestIds = requestList.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        Map<Long, List<Item>> itemsByRequests = new HashMap<>();
        if (!requestIds.isEmpty()) {
            itemsByRequests = itemRepository.findByRequestIdIn(requestIds, Sort.by(Sort.Direction.DESC, "id"))
                    .stream()
                    .collect(Collectors.groupingBy(Item::getRequestId));
        }
        List<ItemRequestDto> result = new ArrayList<>();
        for (ItemRequest itemRequest : requestList) {
            List<ItemDto> itemsList = itemsByRequests.getOrDefault(itemRequest.getId(), new ArrayList<>())
                    .stream().map(itemMapper::toItemDto).collect(toList());
            ItemRequestDto dto = itemRequestMapper.toItemRequestDto(itemRequest, itemsList, user);
            result.add(dto);
        }
        return result;
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден с ID={}", userId));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Запрос не найден с ID={}", requestId));

        List<ItemDto> items = itemRepository.findAllByRequestId(itemRequest.getId()).stream().map(itemMapper::toItemDto).collect(toList());

        return itemRequestMapper.toItemRequestDto(itemRequest, items, user);
    }
}
