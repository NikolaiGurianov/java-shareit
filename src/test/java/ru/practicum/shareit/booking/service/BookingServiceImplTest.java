package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private final ItemRepository itemRepository;
    @Mock
    private final BookingRepository bookingRepository;
    @Mock
    private final UserRepository userRepository;

    private final BookingMapper bookingMapper = new BookingMapper();
    private IncomingBookingDto incomingBookingDto1;
    private IncomingBookingDto incomingBookingDto2;
    private User owner;
    private Item item1;
    private Booking booking;
    private Booking booking1;

    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookingService = new BookingServiceImpl(userRepository, itemRepository, bookingRepository, bookingMapper);

        owner = new User();
        owner.setId(1L);
        owner.setName("UserTestName1");
        owner.setEmail("test@test.ru1");

        User booker = new User();
        booker.setId(2L);
        booker.setName("UserTestName2");
        booker.setEmail("test2@test.ru2");

        item1 = new Item();
        item1.setId(1L);
        item1.setName("TestItem1");
        item1.setDescription("DescriptionOfItemTest");
        item1.setAvailable(true);
        item1.setOwner(owner);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("TestItem2");
        item2.setDescription("DescriptionOfItemTest2");
        item2.setAvailable(true);
        item2.setOwner(owner);

        incomingBookingDto1 = new IncomingBookingDto();
        incomingBookingDto1.setBookerId(2L);
        incomingBookingDto1.setItemId(1L);
        incomingBookingDto1.setStart(LocalDateTime.now().minusDays(1));
        incomingBookingDto1.setEnd(LocalDateTime.now().plusDays(1));
        incomingBookingDto1.setStatus(Status.APPROVED);

        booking = bookingMapper.toBooking(incomingBookingDto1, booker, item1);
        booking.setId(1L);
        bookingDto = bookingMapper.toBookingDto(booking);

        incomingBookingDto2 = new IncomingBookingDto();
        incomingBookingDto2.setBookerId(2L);
        incomingBookingDto2.setItemId(1L);
        incomingBookingDto2.setStart(LocalDateTime.now().plusDays(2));
        incomingBookingDto2.setEnd(LocalDateTime.now().plusDays(3));
        incomingBookingDto2.setStatus(Status.WAITING);

        booking1 = bookingMapper.toBooking(incomingBookingDto2, booker, item1);
        booking1.setId(2L);

    }

    @Test
    public void whenAddBookingIsSuccess() {
        BookingDto expected = bookingDto;

        lenient().when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        lenient().when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto actual = bookingService.addBooking(incomingBookingDto2, 2L);

        assertEquals(actual, expected);
    }

    @Test
    public void whenAddBookingForOwnItemIsNotSuccess() {
        lenient().when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(incomingBookingDto1, 1L));
    }

    @Test
    public void whenAddBookingWithInvalidDatesIsNotSuccess() {
        incomingBookingDto1.setStart(LocalDateTime.now().plusDays(2));
        incomingBookingDto1.setEnd(LocalDateTime.now().plusDays(1));

        lenient().when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        assertThrows(ValidException.class, () -> bookingService.addBooking(incomingBookingDto1, 3L));
    }

    @Test
    public void whenAddBookingWithEndBeforeStartIsNotSuccess() {
        incomingBookingDto1.setStart(LocalDateTime.now().plusDays(2));
        incomingBookingDto1.setEnd(LocalDateTime.now().plusDays(1));

        lenient().when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        assertThrows(ValidException.class, () -> bookingService.addBooking(incomingBookingDto1, 3L));
    }

    @Test
    public void whenGetBookingByIdIsSuccess() {
        BookingDto expected = bookingDto;

        lenient().when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        lenient().when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDto actual = bookingService.getBookingById(booking.getId(), owner.getId());

        assertEquals(expected, actual);
    }

    @Test
    void whenGetBookingByIdWithWrongBookingIdIsNotSuccess() {
        Long bookingId = 99L;

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(bookingId, owner.getId()));
    }

    @Test
    void whenGetBookingByIdWithWrongUserIdIsNotSuccess() {
        Long userId = 99L;
        Long bookingId = 1L;

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(bookingId, userId));
    }

    @Test
    void whenUpdateBookingIsSuccess() {
        IncomingBookingDto updatedBookingDto = new IncomingBookingDto();
        updatedBookingDto.setStart(LocalDateTime.now().plusDays(3));
        updatedBookingDto.setEnd(LocalDateTime.now().plusDays(4));
        updatedBookingDto.setStatus(Status.APPROVED);

        lenient().when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        lenient().when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDto updatedBooking = bookingService.updateBooking(updatedBookingDto, booking.getId(), owner.getId());

        assertEquals(updatedBookingDto.getStart(), updatedBooking.getStart());
        assertEquals(updatedBookingDto.getEnd(), updatedBooking.getEnd());
        assertEquals(updatedBookingDto.getStatus(), updatedBooking.getStatus());
    }

    @Test
    void whenUpdateBookingWithInvalidOwnerIsNotSuccess() {
        IncomingBookingDto updatedBookingDto = new IncomingBookingDto();
        updatedBookingDto.setStart(LocalDateTime.now().plusDays(3));
        updatedBookingDto.setEnd(LocalDateTime.now().plusDays(4));
        updatedBookingDto.setStatus(Status.APPROVED);

        User otherUser = new User();
        otherUser.setId(2L);

        lenient().when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        lenient().when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(ErrorException.class, () -> bookingService.updateBooking(updatedBookingDto, booking.getId(), otherUser.getId()));
    }

    @Test
    void whenUpdateBookingWithInvalidBookingIdIsNotSuccess() {
        IncomingBookingDto updatedBookingDto = new IncomingBookingDto();
        updatedBookingDto.setStart(LocalDateTime.now().plusDays(3));
        updatedBookingDto.setEnd(LocalDateTime.now().plusDays(4));
        updatedBookingDto.setStatus(Status.APPROVED);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.updateBooking(updatedBookingDto, 99L, owner.getId()));
    }


    @Test
    public void whenApprovingBookingIsSuccess() {
        BookingDto expected = bookingMapper.toBookingDto(booking1);

        lenient().when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        lenient().when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));

        BookingDto actual = bookingService.approvingBooking(booking1.getId(), owner.getId(), true);
        expected.setStatus(Status.APPROVED);

        assertEquals(actual, expected);
        assertEquals(Status.APPROVED, booking.getStatus());
    }

    @Test
    public void whenApprovingBookingRejectedIsSuccess() {
        booking.setStatus(Status.REJECTED);
        BookingDto expected = bookingMapper.toBookingDto(booking);

        lenient().when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        lenient().when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDto actual = bookingService.approvingBooking(booking.getId(), owner.getId(), false);

        assertEquals(actual, expected);
        assertEquals(Status.REJECTED, booking.getStatus());
    }

    @Test
    public void whenApprovingBookingAlreadyApprovedIsNotSuccess() {
        booking.setStatus(Status.APPROVED);

        lenient().when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        lenient().when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(ValidException.class, () -> bookingService.approvingBooking(booking.getId(), owner.getId(), true));
        assertEquals(Status.APPROVED, booking.getStatus());
    }

    @Test
    public void whenApprovingBookingWithNonExistentBookingIdIsNotSuccess() {
        Long nonExistentBookingId = 999L;

        when(bookingRepository.findById(nonExistentBookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.approvingBooking(nonExistentBookingId, owner.getId(), true));
    }


    @Test
    public void whenGetBookingsByOwnerIsSuccess() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        bookings.add(booking1);

        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemIdIn(any(), any(), any())).thenReturn(bookings);

        List<BookingDto> expected = bookings.stream().map(bookingMapper::toBookingDto).collect(Collectors.toList());
        List<BookingDto> actual = bookingService.getBookingsByOwner(owner.getId(), State.ALL, 0, 10);

        assertEquals(expected, actual);
    }

    @Test
    void testIsAvailable() {
        boolean result = bookingService.isAvailable(booking, booking1);
        assertFalse(result);
    }

    @Test
    void testBookingIsValid() {
        assertDoesNotThrow(() -> bookingService.bookingIsValid(incomingBookingDto2, item1));
    }
}