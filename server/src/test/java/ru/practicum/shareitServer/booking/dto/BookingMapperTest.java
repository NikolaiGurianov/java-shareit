package ru.practicum.shareitServer.booking.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareitServer.booking.dto.BookingDto;
import ru.practicum.shareitServer.booking.dto.BookingMapper;
import ru.practicum.shareitServer.booking.dto.IncomingBookingDto;
import ru.practicum.shareitServer.booking.model.Booking;
import ru.practicum.shareitServer.booking.model.Status;
import ru.practicum.shareitServer.item.model.Item;
import ru.practicum.shareitServer.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingMapperTest {
    private final BookingMapper bookingMapper = new BookingMapper();

    @Test
    public void whenToBookingIsSuccess() {
        IncomingBookingDto incomingBookingDto = new IncomingBookingDto();
        User booker = new User();
        Item item = new Item();

        incomingBookingDto.setStart(LocalDateTime.now());
        incomingBookingDto.setEnd(LocalDateTime.now().plusDays(2));
        incomingBookingDto.setStatus(Status.WAITING);

        Booking booking = bookingMapper.toBooking(incomingBookingDto, booker, item);

        assertEquals(incomingBookingDto.getStart(), booking.getStart());
        assertEquals(incomingBookingDto.getEnd(), booking.getEnd());
        assertEquals(item, booking.getItem());
        assertEquals(booker, booking.getBooker());
        assertEquals(incomingBookingDto.getStatus(), booking.getStatus());
    }

    @Test
    public void whenToBookingDtoIsSuccess() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(new Item());
        booking.setBooker(new User(1L, "Steave", "ww@mail"));
        booking.setStatus(Status.WAITING);

        BookingDto bookingDto = bookingMapper.toBookingDto(booking);

        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
    }
}


