package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ErrorException;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody IncomingBookingDto incomingBookingDto) {
        log.info("Получен запрос на бронирование вещи от пользователя ID={}", userId);
        return bookingService.addBooking(incomingBookingDto, userId);
    }


    @PatchMapping(value = "/{bookingId}", consumes = "application/json")
    public BookingDto updateBooking(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestBody(required = false) IncomingBookingDto incomingBookingDto,
            @PathVariable Long bookingId,
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
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId) {
        log.info("Получен запрос на выдачу данных бронирования с ID={}", bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findBookingByBooker(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @RequestParam(required = false, defaultValue = "ALL") State state,
            @RequestParam(defaultValue = "0") @Min(0) @Positive Integer from,
            @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        log.info("Получен запрос на выдачу вещей, забронированных пользователем с ID = {}", bookerId);
        if (from >= 0 && size > 0) {
            return bookingService.getBookingsByBooker(bookerId, state, from, size);
        }
        throw new ErrorException("Заданы неправильные параметры");
    }

    @GetMapping("/owner")
    public List<BookingDto> findBookingByOwner(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(required = false, defaultValue = "ALL") State state,
            @RequestParam(defaultValue = "0") @Min(0) @Positive Integer from,
            @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        log.info("Получен запрос на выдачу вещей, принадлежащих пользователю с ID = {}", ownerId);
        if (from >= 0 && size > 0) {
            return bookingService.getBookingsByOwner(ownerId, state, from, size);
        }
        throw new ErrorException("Заданы неправильные параметры");
    }
}
