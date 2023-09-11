package ru.practicum.shareit.booking.dto;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

@Component
@Data
public class BookingMapper {
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    public Booking toBooking(IncomingBookingDto incomingBookingDto, User booker, Item item) {
        Booking booking = new Booking();
        booking.setStart(incomingBookingDto.getStart());
        booking.setEnd(incomingBookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(incomingBookingDto.getStatus());
        return booking;
    }

    public BookingDto toBookingDto(Booking booking) {
        BookerDto bookerDto = new BookerDto(booking.getBooker().getId());
        BookedItemDto item = new BookedItemDto(booking.getItem().getId(), booking.getItem().getName());
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItem(item);
        bookingDto.setBooker(bookerDto);
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

}
