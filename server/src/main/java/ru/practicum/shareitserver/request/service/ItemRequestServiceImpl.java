package ru.practicum.shareitserver.request.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareitserver.exception.NotFoundException;
import ru.practicum.shareitserver.item.dto.ItemDto;
import ru.practicum.shareitserver.item.dto.ItemMapper;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.item.repository.ItemRepository;
import ru.practicum.shareitserver.request.dto.ItemRequestDto;
import ru.practicum.shareitserver.request.dto.ItemRequestMapper;
import ru.practicum.shareitserver.request.model.ItemRequest;
import ru.practicum.shareitserver.request.repository.ItemRequestRepository;
import ru.practicum.shareitserver.user.model.User;
import ru.practicum.shareitserver.user.repository.UserRepository;
import ru.practicum.shareitserver.util.Constant;

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
        PageRequest pageRequest = PageRequest.of(0, 20, Constant.SORT_BY_DESC_CREATED);

        List<ItemRequest> requestsList = itemRequestRepository.findAllByRequesterId(userId, pageRequest);

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

        PageRequest pageRequest = PageRequest.of(from / size, size, Constant.SORT_BY_DESC_CREATED);
        List<ItemRequest> requestList =
                itemRequestRepository.findAllByRequesterIdNot(userId, pageRequest);

        List<Long> requestIds = requestList.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        Map<Long, List<Item>> itemsByRequests = itemRepository.findByRequestIdIn(requestIds, Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .collect(Collectors.groupingBy(Item::getRequestId));

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
