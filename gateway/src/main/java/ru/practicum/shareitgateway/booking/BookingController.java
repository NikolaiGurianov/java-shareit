package ru.practicum.shareitgateway.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.booking.dto.BookItemRequestDto;
import ru.practicum.shareitgateway.booking.dto.BookingState;
import ru.practicum.shareitgateway.exception.ValidException;
import ru.practicum.shareitgateway.exception.UnknownStateException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        if (requestDto.getEnd().isBefore(requestDto.getStart())) {
            throw new ValidException("Дата окончания бронирования раньше начала бронирования!");
        }
        if (requestDto.getEnd().isEqual(requestDto.getStart())) {
            throw new ValidException("Время начала и окончания бронировая совпадают!");
        }
        return bookingClient.bookItem(userId, requestDto);
    }

    @PatchMapping(value = "/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                 @PathVariable long bookingId,
                                                 @RequestParam(required = false) Boolean approved) {
        return bookingClient.updateBooking(bookingId, ownerId, approved);
    }

    @GetMapping(value = "/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable long bookingId) {
        log.info("Get booking by bookingId={}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByBooker(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                      @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                      @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new UnknownStateException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, bookerId, from, size);
        return bookingClient.getBookings(bookerId, state, from, size);
    }

    @GetMapping(value = "/owner")
    public ResponseEntity<Object> getBookingsForOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                      @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                      @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, ownerId, from, size);
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new UnknownStateException("Unknown state: " + stateParam));
        return bookingClient.getBookingsForOwner(ownerId, state, from, size);
    }
}