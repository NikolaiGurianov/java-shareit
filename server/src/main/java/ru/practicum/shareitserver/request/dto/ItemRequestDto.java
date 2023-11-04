package ru.practicum.shareitserver.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareitserver.item.dto.ItemDto;
import ru.practicum.shareitserver.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private long id;
    private String description;
    private User requester;
    private LocalDateTime created;
    List<ItemDto> items;
}
