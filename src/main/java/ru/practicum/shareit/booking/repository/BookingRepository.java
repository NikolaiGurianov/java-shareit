package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

@Component
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByBookerId(Long bookerId, PageRequest pageRequest, Sort sort);

    List<Booking> findAllByBooker_IdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime dateTime,
                                                              LocalDateTime dateTime1, Sort sort);

    List<Booking> findAllByBooker_IdAndEndBefore(Long bookerId, LocalDateTime dateTime, Sort sort);

    List<Booking> findAllByBooker_IdAndStartAfter(Long bookerId, LocalDateTime dateTime, Sort sort);

    List<Booking> findAllByBooker_IdAndStatus(Long bookerId, Status status, Sort sort);


    List<Booking> findByItemIdIn(List<Long> itemIds, Pageable pageable);

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

    @Query("SELECT b FROM Booking b " +
            "INNER JOIN b.item i " +
            "WHERE i.owner.id = ?1 AND ?2 BETWEEN b.start AND b.end")
    Page<Booking> findCurrentForDateByOwner(Long ownerId, LocalDateTime date, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "INNER JOIN b.item i " +
            "WHERE i.owner.id = ?1 AND b.start > ?2")
    Page<Booking> findFutureForDateByOwner(Long ownerId, LocalDateTime date, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "INNER JOIN b.item i " +
            "WHERE i.owner.id = ?1 AND b.end < ?2")
    Page<Booking> findPastForDateByOwner(Long ownerId, LocalDateTime date, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "INNER JOIN b.item i " +
            "WHERE i.owner.id = ?1 " +
            "AND b.status = 'WAITING'")
    Page<Booking> findWaitingForDateByOwner(Long ownerId, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "INNER JOIN b.item i " +
            "WHERE i.owner.id = ?1 " +
            "AND b.status = 'REJECTED'")
    Page<Booking> findRejectedForDateByOwner(Long ownerId, Pageable pageable);

}