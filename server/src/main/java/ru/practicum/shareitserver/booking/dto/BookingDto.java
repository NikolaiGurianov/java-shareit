package ru.practicum.shareitserver.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareitserver.booking.model.Status;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookedItemDto item;
    private BookerDto booker;
    private Status status;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookerDto {
        private Long id;
    }

}
