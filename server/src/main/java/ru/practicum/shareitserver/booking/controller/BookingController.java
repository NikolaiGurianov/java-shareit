package ru.practicum.shareitserver.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitserver.booking.dto.BookingDto;
import ru.practicum.shareitserver.booking.dto.IncomingBookingDto;
import ru.practicum.shareitserver.booking.model.State;
import ru.practicum.shareitserver.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody IncomingBookingDto incomingBookingDto) {
        log.info("Получен запрос на бронирование вещи от пользователя ID={}", userId);
        return bookingService.addBooking(incomingBookingDto, userId);
    }


    @PatchMapping(value = "/{bookingId}", consumes = "application/json")
    public BookingDto updateBooking(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestBody(required = false) IncomingBookingDto incomingBookingDto,
            @PathVariable long bookingId,
            @RequestParam(required = false) Boolean approved) {
        log.info("Получен запрос на обновление вещи пользователем с ID = {}", ownerId);

        if (approved != null) {
            log.info("Получен запрос на подтверждение бронирования id = {} пользователем с id = {}", bookingId, ownerId);
            return bookingService.approvingBooking(bookingId, ownerId, approved);
        }
        return bookingService.updateBooking(incomingBookingDto, bookingId, ownerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId) {
        log.info("Получен запрос на выдачу данных бронирования с ID={}", bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findBookingByBooker(
            @RequestHeader("X-Sharer-User-Id") long bookerId,
            @RequestParam(required = false, defaultValue = "ALL") State state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Получен запрос на выдачу вещей, забронированных пользователем с ID = {}", bookerId);
        return bookingService.getBookingsByBooker(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> findBookingByOwner(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(required = false, defaultValue = "ALL") State state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Получен запрос на выдачу вещей, принадлежащих пользователю с ID = {}", ownerId);
        return bookingService.getBookingsByOwner(ownerId, state, from, size);
    }
}
