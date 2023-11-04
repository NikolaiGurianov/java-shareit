package ru.practicum.shareitserver.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookedItemDto {
    private Long id;
    private String name;
}
