package ru.practicum.shareitserver.booking.dto;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareitserver.booking.model.Booking;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.user.model.User;

@Component
@Data
public class BookingMapper {

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
        BookedItemDto item = new BookedItemDto(booking.getItem().getId(), booking.getItem().getName());
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItem(item);
        bookingDto.setBooker(new BookingDto.BookerDto(booking.getBooker().getId()));
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

}
