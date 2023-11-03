package ru.practicum.shareitServer.booking.service;

import ru.practicum.shareitServer.booking.dto.BookingDto;
import ru.practicum.shareitServer.booking.dto.IncomingBookingDto;
import ru.practicum.shareitServer.booking.model.State;

import java.util.List;

public interface BookingService {
    BookingDto getBookingById(Long id, Long userId);

    List<BookingDto> getBookingsByBooker(Long bookerId, State state, Integer from, Integer size);

    BookingDto addBooking(IncomingBookingDto incomingBookingDto, Long bookerId);

    BookingDto updateBooking(IncomingBookingDto incomingBookingDto, Long bookingId, Long bookerId);

    BookingDto approvingBooking(Long bookingId, Long bookerId, Boolean approved);

    List<BookingDto> getBookingsByOwner(Long ownerId, State state, Integer from, Integer size);
}

