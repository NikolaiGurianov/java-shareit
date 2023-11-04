package ru.practicum.shareitserver.item.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import ru.practicum.shareitserver.item.model.Item;

import java.util.List;

@Component
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = "select * " +
            "from items " +
            "where owner_id = ?1",
            nativeQuery = true)
    List<Item> findAllByOwnerId(Long userId, PageRequest pageRequest);

    @Query("SELECT i.id FROM Item i WHERE i.owner.id = :ownerId")
    List<Long> findItemIdsByOwner_Id(@Param("ownerId") Long ownerId);


    List<Item> findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(
            String textInName,
            String textInDescription,
            PageRequest pageRequest);

    List<Item> findAllByRequestId(Long requestId);

    List<Item> findByRequestIdIn(List<Long> itemRequests, Sort created);
}
