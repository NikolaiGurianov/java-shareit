package ru.practicum.shareit.booking.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.Constant;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto getBookingById(Long bookingId, Long ownerId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()
                -> new NotFoundException("Бронь с ID={} не найдена", bookingId));
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(()
                -> new NotFoundException("Вещь не найдена"));
        if (!booking.getBooker().getId().equals(ownerId) && !item.getOwnerId().equals(ownerId))
            throw new NotFoundException("Даные доступны только для владельца вещи или автора брони");
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto addBooking(IncomingBookingDto incomingBookingDto, Long bookerId) {
        long itemId = incomingBookingDto.getItemId();
        User booker = userRepository.findById(bookerId).orElseThrow(()
                -> new NotFoundException("Пользователь с ID={} не найден", bookerId));
        Item item = itemRepository.findById(itemId).orElseThrow(()
                -> new NotFoundException("Вещь с ID={} не найдена", itemId));
        if (item.getOwnerId().equals(bookerId))
            throw new NotFoundException("Владелец вещи или автор бронирования совпадают");
        bookingIsValid(incomingBookingDto, item);
        Booking booking = bookingMapper.toBooking(incomingBookingDto, booker, item);
        List<Booking> allBookingsByItem = bookingRepository.findByItem_Id(itemId);
        boolean isOverlap = allBookingsByItem
                .stream()
                .map(booking1 -> isAvailable(booking1, booking))
                .reduce(Boolean::logicalOr).orElse(false);

        if (isOverlap) {
            throw new NotFoundException("Данная вещь на этот период недоступна");
        }
        booking.setStatus(Status.WAITING);
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    public boolean isAvailable(Booking one, Booking two) {
        return one.getStart().isBefore(two.getEnd()) && two.getStart().isBefore(one.getEnd());
    }

    public void bookingIsValid(IncomingBookingDto incomingBookingDto, Item item) {
        LocalDateTime start = incomingBookingDto.getStart();
        LocalDateTime end = incomingBookingDto.getEnd();
        if (start == null || end == null)
            throw new ValidException("Дата начала и окончания бронирования должна быть заполнена");
        if (start.isBefore(LocalDateTime.now()) || end.isBefore(start) || start.equals(end))
            throw new ValidException("Указано не корректное время начала или окончания аренды");
        if (!item.getAvailable())
            throw new ValidException("Данная вещь не доступна для бронирования");
    }

    @Override
    public BookingDto updateBooking(IncomingBookingDto incomingBookingDto, Long bookingId, Long ownerId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()
                -> new NotFoundException("Бронь с ID={} не найдена", bookingId));
        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new NotFoundException("Вещь с ID={} не найдена", booking.getItem().getId()));
        if (!ownerId.equals(item.getOwnerId()))
            throw new ErrorException("Обновление брони может выполнять только владелец вещи");
        booking.setStart(incomingBookingDto.getStart());
        booking.setEnd(incomingBookingDto.getEnd());
        booking.setStatus(incomingBookingDto.getStatus());
        bookingRepository.save(booking);
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto approvingBooking(Long bookingId, Long ownerId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с ID = {} не найдено", bookingId));
        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new NotFoundException("Вещь с ID = {} не найдена", booking.getItem().getId()));
        if (!ownerId.equals(item.getOwnerId()))
            throw new NotFoundException("Подтвержение может быть выполнено только владельцем вещи");
        if (approved != null) {
            if (approved) {
                if (booking.getStatus() == Status.APPROVED) {
                    throw new ValidException("Статус уже подтвержден");
                }
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
            bookingRepository.save(booking);
        }
        return bookingMapper.toBookingDto(booking);
    }


    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getBookingsByOwner(Long ownerId, State state, Integer from, Integer size) {
        LocalDateTime now = LocalDateTime.now();

        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID = {} не найден", ownerId));

        Pageable pageable = PageRequest.of(from / size, size, Constant.SORT_BY_DESC);

        switch (state) {
            case ALL:
                return bookingRepository.findByItemIdIn(itemRepository.findItemIdsByOwner_Id(ownerId), pageable)
                        .stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());

            case CURRENT:
                return bookingRepository.findCurrentForDateByOwner(ownerId, now, pageable)
                        .getContent()
                        .stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());

            case FUTURE:
                return bookingRepository.findFutureForDateByOwner(ownerId, now, pageable)
                        .getContent()
                        .stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());

            case PAST:
                return bookingRepository.findPastForDateByOwner(ownerId, now, pageable)
                        .getContent()
                        .stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());

            case WAITING:
                return bookingRepository.findWaitingForDateByOwner(ownerId, pageable)
                        .getContent()
                        .stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());

            case REJECTED:
                return bookingRepository.findRejectedForDateByOwner(ownerId, pageable)
                        .getContent()
                        .stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                throw new UnknownStateException("Unknown state: " + state);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getBookingsByBooker(Long bookerId, State state, Integer from, Integer size) {
        userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID={} не найден", bookerId));
        List<Booking> bookings;
        List<BookingDto> bookingsDto = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        PageRequest pageRequest = PageRequest.of(from / size, size);
        Page<Booking> bookingList = bookingRepository.findAllByBookerId(bookerId, pageRequest, Constant.SORT_BY_DESC);

        switch (state) {
            case ALL:

                for (Booking booking : bookingList) {
                    bookingsDto.add(bookingMapper.toBookingDto(booking));
                }
                return bookingsDto;

            case CURRENT:
                bookings = bookingRepository.findAllByBooker_IdAndStartBeforeAndEndAfter(bookerId,
                        now, now, Constant.SORT_BY_DESC);
                for (Booking booking : bookings) {
                    bookingsDto.add(bookingMapper.toBookingDto(booking));
                }
                return bookingsDto;
            case FUTURE:
                bookings = bookingRepository.findAllByBooker_IdAndStartAfter(bookerId, now, Constant.SORT_BY_DESC);
                for (Booking booking : bookings) {
                    bookingsDto.add(bookingMapper.toBookingDto(booking));
                }
                return bookingsDto;
            case PAST:
                bookings = bookingRepository.findAllByBooker_IdAndEndBefore(bookerId, now, Constant.SORT_BY_DESC);
                for (Booking booking : bookings) {
                    bookingsDto.add(bookingMapper.toBookingDto(booking));
                }
                return bookingsDto;
            case WAITING:
                bookings = bookingRepository.findAllByBooker_IdAndStatus(bookerId, Status.WAITING, Constant.SORT_BY_DESC);
                for (Booking booking : bookings) {
                    bookingsDto.add(bookingMapper.toBookingDto(booking));
                }
                return bookingsDto;
            case REJECTED:
                bookings = bookingRepository.findAllByBooker_IdAndStatus(bookerId, Status.REJECTED, Constant.SORT_BY_DESC);
                for (Booking booking : bookings) {
                    bookingsDto.add(bookingMapper.toBookingDto(booking));
                }
                return bookingsDto;
            default:
                throw new UnknownStateException("Unknown state: " + state);
        }
    }
}



