package ru.practicum.shareitgateway.request.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareitserver.item.dto.ItemDto;
import ru.practicum.shareitserver.user.model.User;

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
