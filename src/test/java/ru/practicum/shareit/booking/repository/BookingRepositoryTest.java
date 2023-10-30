package ru.practicum.shareit.booking.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Constant;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DataJpaTest
public class BookingRepositoryTest {
    private final TestEntityManager entityManager;
    private final BookingRepository bookingRepository;

    private User booker;
    private Item item;
    private Booking booking1;
    private Booking booking2;
    private Booking booking3;

    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        User owner = new User(null, "UserTestName1", "test2@test.ru1");
        booker = new User(null, "UserTestName2", "test2@test.ru2");
        item = new Item(
                null, "TestItem1", "DescriptionOfItemTest", true, owner, null);
        booking1 = new Booking(null, now.minusHours(2), now.plusMinutes(30), item, booker, Status.APPROVED);
        booking2 = new Booking(null, now.plusHours(1), now.plusHours(2), item, booker, Status.WAITING);
        booking3 = new Booking(null, now.plusHours(3), now.plusHours(4), item, booker, Status.APPROVED);

        entityManager.persist(owner);
        entityManager.persist(booker);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.persist(booking3);
    }

    @AfterEach
    void clear() {
        entityManager.clear();
    }

    @Test
    void whenFindAllByBookerIdIsSuccess() {
        Page<Booking> result = bookingRepository.findAllByBookerId(
                booker.getId(),
                PageRequest.of(0, 10, Constant.SORT_BY_DESC));

        assertEquals(3, result.getTotalElements());
    }

    @Test
    void whenFindAllByBooker_IdAndStartBeforeAndEndAfterIsSuccess() {

        List<Booking> result = bookingRepository.findAllByBooker_IdAndStartBeforeAndEndAfter(
                booker.getId(),
                now,
                now,
                null
        );

        assertEquals(1, result.size());
    }

    @Test
    void whenFindAllByBooker_IdAndEndBeforeIsSuccess() {
        List<Booking> result = bookingRepository.findAllByBooker_IdAndEndBefore(
                booker.getId(),
                now.plusHours(3),
                null
        );

        assertEquals(2, result.size());
    }

    @Test
    void whenFindAllByBooker_IdAndStartAfterIsSuccess() {
        List<Booking> result = bookingRepository.findAllByBooker_IdAndStartAfter(
                booker.getId(),
                now.plusMinutes(30),
                null
        );

        assertEquals(2, result.size());
    }

    @Test
    void whenFindAllByBooker_IdAndStatusIsSuccess() {
        List<Booking> result = bookingRepository.findAllByBooker_IdAndStatus(
                booker.getId(),
                Status.APPROVED,
                null);

        assertEquals(2, result.size());
    }


    @Test
    void whenFindByItem_IdIsSuccess() {
        List<Booking> result = bookingRepository.findByItem_Id(item.getId());

        assertEquals(List.of(booking1, booking2, booking3), result);
    }

    @Test
    void whenFindLastIsSuccess() {
        Booking result = bookingRepository.findLast(item.getId(), now.minusMinutes(30));

        assertEquals(booking1, result);
    }

    @Test
    void whenFindNextIsSuccess() {
        Booking result = bookingRepository.findNext(item.getId(), now.plusMinutes(30));

        assertEquals(booking3, result);
    }

    @Test
    void whenExistsByBooker_IdAndEndBeforeAndStatusIsSuccess() {
        boolean result = bookingRepository.existsByBooker_IdAndEndBeforeAndStatus(
                booker.getId(),
                now.plusHours(1),
                Status.WAITING
        );

        assertFalse(result);
    }

    @Test
    void whenFindCurrentForDateByOwnerIsSuccess() {
        Page<Booking> result = bookingRepository.findCurrentForDateByOwner(
                item.getOwner().getId(),
                now.plusMinutes(15),
                PageRequest.of(0, 10)
        );

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void whenFindFutureForDateByOwnerIsSuccess() {
        Page<Booking> result = bookingRepository.findFutureForDateByOwner(
                item.getOwner().getId(),
                now.minusMinutes(15),
                PageRequest.of(0, 10)
        );

        assertEquals(2, result.getTotalElements());
    }

    @Test
    void whenFindPastForDateByOwnerIsSuccess() {
        Page<Booking> result = bookingRepository.findPastForDateByOwner(
                item.getOwner().getId(),
                now.plusHours(4),
                PageRequest.of(0, 10)
        );
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void whenFindWaitingForDateByOwnerIsSuccess() {
        Page<Booking> result = bookingRepository.findWaitingForDateByOwner(
                item.getOwner().getId(),
                PageRequest.of(0, 10)
        );

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void whenFindRejectedForDateByOwnerIsSuccess() {
        Page<Booking> result = bookingRepository.findRejectedForDateByOwner(
                item.getOwner().getId(),
                PageRequest.of(0, 10)
        );

        assertEquals(0, result.getTotalElements());
    }
}
