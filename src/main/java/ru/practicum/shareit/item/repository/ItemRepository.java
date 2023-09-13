package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Component
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = "select * "
            + "from items "
            + "where owner_id = ?1",
            nativeQuery = true)
    List<Item> findAllByOwnerId(Long userId);

    @Query("SELECT i.id FROM Item i WHERE i.owner.id = :ownerId")
    List<Long> findItemIdsByOwner_Id(@Param("ownerId") Long ownerId);


    List<Item> findByNameIsContainingIgnoreCaseOrDescriptionIsContainingIgnoreCase(String textInName, String textInDescription);
}
