package ru.practicum.shareitserver.request.dto;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareitserver.item.dto.ItemDto;
import ru.practicum.shareitserver.request.model.ItemRequest;
import ru.practicum.shareitserver.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Data
public class ItemRequestMapper {

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequester(user);
        itemRequest.setCreated(itemRequestDto.getCreated() != null ? itemRequestDto.getCreated() : LocalDateTime.now());
        return itemRequest;
    }

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<ItemDto> itemList, User user) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setRequester(user);
        itemRequestDto.setItems(itemList);
        itemRequestDto.setCreated(itemRequest.getCreated());
        return itemRequestDto;
    }


}
