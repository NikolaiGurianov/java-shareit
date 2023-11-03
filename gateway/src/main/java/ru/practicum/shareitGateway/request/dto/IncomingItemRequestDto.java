package ru.practicum.shareitGateway.request.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareitServer.item.dto.ItemDto;
import ru.practicum.shareitServer.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import java.util.List;

@Data
@RequiredArgsConstructor
public class IncomingItemRequestDto {
    private long id;
    @NotNull(message = "Описание не может быть пустым")
    private String description;
    private User requester;
    private LocalDateTime created;
    List<ItemDto> items;
}
