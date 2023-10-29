package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Status;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id;
    @NotNull
    private LocalDateTime start;
    @NotNull
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
