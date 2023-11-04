package ru.practicum.shareitserver.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareitserver.booking.dto.BookingDto;
import ru.practicum.shareitserver.booking.dto.BookingMapper;
import ru.practicum.shareitserver.booking.dto.IncomingBookingDto;
import ru.practicum.shareitserver.booking.model.Booking;
import ru.practicum.shareitserver.booking.model.State;
import ru.practicum.shareitserver.booking.model.Status;
import ru.practicum.shareitserver.booking.repository.BookingRepository;
import ru.practicum.shareitserver.exception.NotFoundException;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.item.repository.ItemRepository;
import ru.practicum.shareitserver.user.model.User;
import ru.practicum.shareitserver.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {
    private BookingService bookingService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    private final BookingMapper bookingMapper = new BookingMapper();

    private User owner;
    private User booker;

    private IncomingBookingDto incomingBookingDto1;

    private IncomingBookingDto incomingBookingDto2;


    private Booking bookingCurrent;
    private Booking bookingPast;

    private final int from = 0;
    private final int size = 20;

    @BeforeEach
    void initDb() {
        bookingService = new BookingServiceImpl(userRepository, itemRepository, bookingRepository, bookingMapper);
        owner = new User(null, "owner", "owner@email.com");
        userRepository.save(owner);
        booker = new User(null, "booker", "booker@email.com");
        userRepository.save(booker);

        Item item1 = new Item(null, "item1", "item1 description", true, owner, null);
        itemRepository.save(item1);

        Item item2 = new Item(null, "item2", "item2 description", true, owner, null);
        itemRepository.save(item2);

        bookingCurrent = bookingRepository.save(new Booking(null, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(1), item1, booker, Status.APPROVED));
        bookingPast = bookingRepository.save(new Booking(null, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item1, booker, Status.APPROVED));

        incomingBookingDto1 = new IncomingBookingDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item1.getId(), booker.getId(), Status.WAITING);

        incomingBookingDto2 = new IncomingBookingDto(LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), item2.getId(), booker.getId(), Status.WAITING);
    }

    @AfterEach
    void clearDb() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    @Transactional
    @DirtiesContext
    void getAllBookingsByBookerAllStateTest() {
        BookingDto bookingDto1 = bookingService.addBooking(incomingBookingDto1, booker.getId());
        BookingDto bookingDto2 = bookingService.addBooking(incomingBookingDto2, booker.getId());

        Collection<BookingDto> bookings = bookingService.getBookingsByBooker(booker.getId(), State.ALL, from, size);

        assertEquals(4, bookings.size());
        assertTrue(bookings.containsAll(List.of(bookingDto1, bookingDto2)));
    }

    @Test
    @Transactional
    @DirtiesContext
    void getAllBookingsForOwnerAllStateItems() {
        BookingDto bookingDto1 = bookingService.addBooking(incomingBookingDto1, booker.getId());
        BookingDto bookingDto2 = bookingService.addBooking(incomingBookingDto2, booker.getId());

        Collection<BookingDto> bookings = bookingService.getBookingsByOwner(owner.getId(), State.ALL, from, size);

        assertEquals(4, bookings.size());
        assertTrue(bookings.containsAll(List.of(bookingDto1, bookingDto2)));
    }

    @Test
    @Transactional
    void getAllBookingsByBookerCurrentStateTest() {
        BookingDto bookingDto1 = bookingService.addBooking(incomingBookingDto1, booker.getId());
        BookingDto bookingDto2 = bookingService.addBooking(incomingBookingDto2, booker.getId());

        Collection<BookingDto> bookings = bookingService.getBookingsByBooker(booker.getId(), State.CURRENT, from, size);

        assertEquals(1, bookings.size());
        assertFalse(bookings.containsAll(List.of(bookingDto1, bookingDto2)));
        assertTrue(bookings.contains(bookingMapper.toBookingDto(bookingCurrent)));
    }

    @Test
    @Transactional
    void getAllBookingsForOwnerCurrentStateItems() {
        BookingDto bookingDto1 = bookingService.addBooking(incomingBookingDto1, booker.getId());
        BookingDto bookingDto2 = bookingService.addBooking(incomingBookingDto2, booker.getId());

        Collection<BookingDto> bookings = bookingService.getBookingsByOwner(owner.getId(), State.CURRENT, from, size);

        assertEquals(1, bookings.size());
        assertFalse(bookings.containsAll(List.of(bookingDto1, bookingDto2)));
        assertTrue(bookings.contains(bookingMapper.toBookingDto(bookingCurrent)));
    }

    @Test
    @Transactional
    void getAllBookingsByBookerPastStateTest() {
        BookingDto bookingDto1 = bookingService.addBooking(incomingBookingDto1, booker.getId());
        BookingDto bookingDto2 = bookingService.addBooking(incomingBookingDto2, booker.getId());

        Collection<BookingDto> bookings = bookingService.getBookingsByBooker(booker.getId(), State.PAST, from, size);

        assertEquals(1, bookings.size());
        assertFalse(bookings.containsAll(List.of(bookingDto1, bookingDto2)));
        assertTrue(bookings.contains(bookingMapper.toBookingDto(bookingPast)));
    }

    @Test
    @Transactional
    void getAllBookingsForOwnerPastStateItems() {
        BookingDto bookingDto1 = bookingService.addBooking(incomingBookingDto1, booker.getId());
        BookingDto bookingDto2 = bookingService.addBooking(incomingBookingDto2, booker.getId());

        Collection<BookingDto> bookings = bookingService.getBookingsByOwner(owner.getId(), State.PAST, from, size);

        assertEquals(1, bookings.size());
        assertFalse(bookings.containsAll(List.of(bookingDto1, bookingDto2)));
        assertTrue(bookings.contains(bookingMapper.toBookingDto(bookingPast)));
    }

    @Test
    @Transactional
    void getAllBookingsByBookerFutureStateTest() {
        BookingDto bookingDto1 = bookingService.addBooking(incomingBookingDto1, booker.getId());
        BookingDto bookingDto2 = bookingService.addBooking(incomingBookingDto2, booker.getId());

        Collection<BookingDto> bookings = bookingService.getBookingsByBooker(booker.getId(), State.FUTURE, from, size);

        assertEquals(2, bookings.size());
        assertTrue(bookings.containsAll(List.of(bookingDto1, bookingDto2)));
        assertFalse(bookings.containsAll(List.of(bookingMapper.toBookingDto(bookingCurrent), bookingMapper.toBookingDto(bookingPast))));

    }

    @Test
    @Transactional
    void getAllBookingsForOwnerFutureStateItems() {
        BookingDto bookingDto1 = bookingService.addBooking(incomingBookingDto1, booker.getId());
        BookingDto bookingDto2 = bookingService.addBooking(incomingBookingDto2, booker.getId());

        Collection<BookingDto> bookings = bookingService.getBookingsByOwner(booker.getId(), State.FUTURE, from, size);

        assertEquals(0, bookings.size());
        assertFalse(bookings.containsAll(List.of(bookingDto1, bookingDto2)));
    }

    @Test
    @Transactional
    void getAllBookingsByBookerWaitingStateTest() {
        BookingDto bookingDto1 = bookingService.addBooking(incomingBookingDto1, booker.getId());
        BookingDto bookingDto2 = bookingService.addBooking(incomingBookingDto2, booker.getId());

        Collection<BookingDto> bookings = bookingService.getBookingsByBooker(booker.getId(), State.WAITING, from, size);
        assertEquals(2, bookings.size());
        assertTrue(bookings.containsAll(List.of(bookingDto1, bookingDto2)));
    }

    @Test
    @Transactional
    void getAllBookingsForOwnerWaitingStateItems() {
        BookingDto bookingDto1 = bookingService.addBooking(incomingBookingDto1, booker.getId());
        BookingDto bookingDto2 = bookingService.addBooking(incomingBookingDto2, booker.getId());

        Collection<BookingDto> bookings = bookingService.getBookingsByOwner(booker.getId(), State.WAITING, from, size);

        assertEquals(0, bookings.size());
        assertFalse(bookings.containsAll(List.of(bookingDto1, bookingDto2)));
    }

    @Test
    @Transactional
    void getAllBookingsByBookerRejectedStateTest() {
        BookingDto bookingDto1 = bookingService.addBooking(incomingBookingDto1, booker.getId());
        BookingDto bookingDto2 = bookingService.addBooking(incomingBookingDto2, booker.getId());

        Collection<BookingDto> bookings = bookingService.getBookingsByBooker(booker.getId(), State.WAITING, from, size);

        assertEquals(2, bookings.size());
        assertTrue(bookings.containsAll(List.of(bookingDto1, bookingDto2)));
    }

    @Test
    @Transactional
    void getAllBookingsForOwnerRejectedStateItems() {
        BookingDto bookingDto1 = bookingService.addBooking(incomingBookingDto1, booker.getId());
        BookingDto bookingDto2 = bookingService.addBooking(incomingBookingDto2, booker.getId());

        Collection<BookingDto> bookings = bookingService.getBookingsByOwner(booker.getId(), State.WAITING, from, size);

        assertEquals(0, bookings.size());
        assertFalse(bookings.containsAll(List.of(bookingDto1, bookingDto2)));
    }

    @Test
    @Transactional
    public void whenUserNotFound_GetBookingsByOwner_ThrowsNotFoundException() {
        long nonExistentUserId = 99L;

        assertThrows(NotFoundException.class, () -> bookingService.getBookingsByOwner(
                nonExistentUserId, State.ALL, 0, 10));
    }

    @Test
    @Transactional
    public void whenNoItems_GetBookingsByOwner_ReturnsEmptyList() {
        bookingRepository.deleteAll();
        long ownerId = owner.getId();
        List<BookingDto> actual = bookingService.getBookingsByOwner(ownerId, State.ALL, 0, 10);

        List<BookingDto> expected = new ArrayList<>();
        assertEquals(expected, actual);
    }
}
