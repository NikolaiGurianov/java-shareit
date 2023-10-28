package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

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


