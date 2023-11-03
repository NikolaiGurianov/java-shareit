package ru.practicum.shareitServer.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareitServer.booking.dto.BookingLastNextDto;
import ru.practicum.shareitServer.item.comment.dto.CommentDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemLastNextDto {
    private long id;
    private String name;
    private String description;
    private boolean isAvailable;
    private BookingLastNextDto lastBooking;
    private BookingLastNextDto nextBooking;
    private List<CommentDto> comments;
}
