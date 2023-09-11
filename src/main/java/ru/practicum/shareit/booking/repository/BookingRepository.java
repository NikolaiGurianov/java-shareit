package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

@Component
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker_IdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime dateTime,
                                                                              LocalDateTime dateTime1);

    List<Booking> findAllByBooker_IdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime dateTime);

    List<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime dateTime);

    List<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(Long bookerId, Status status);


    List<Booking> findByItemIdInOrderByStartDesc(List<Long> itemIds);

    List<Booking> findByItem_Id(Long itemId);

    @Query(value = "select * " +
            "from BOOKINGS " +
            "where ITEM_ID = ?1 and start_time <= ?2 and status = 'APPROVED' " +
            "order by start_time desc " +
            "limit 1", nativeQuery = true)
    Booking findLast(Long itemId, LocalDateTime dateTime);

    @Query(value = "select * " +
            "from BOOKINGS " +
            "where ITEM_ID = ?1 and start_time >= ?2 and status = 'APPROVED' " +
            "order by start_time " +
            "limit 1", nativeQuery = true)
    Booking findNext(Long itemId, LocalDateTime dateTime);

    boolean existsByBooker_IdAndEndBeforeAndStatus(Long userId, LocalDateTime dateTime, Status status);


}
