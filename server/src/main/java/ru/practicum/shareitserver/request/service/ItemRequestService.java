package ru.practicum.shareitserver.request.service;

import ru.practicum.shareitserver.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long ownerId);

    List<ItemRequestDto> getRequestsByOwner(Long ownerId);

    List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size);

    ItemRequestDto getRequestById(Long userId, Long requestId);

}