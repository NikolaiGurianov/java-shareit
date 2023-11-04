package ru.practicum.shareitserver.item.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.request.model.ItemRequest;
import ru.practicum.shareitserver.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DataJpaTest
class ItemRepositoryTest {
    private final TestEntityManager entityManager;
    private final ItemRepository itemRepository;

    private User user;
    private Item item;
    private Item item2;
    private ItemRequest itemRequest;

    private final PageRequest pageRequest = PageRequest.of(0, 3);

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Test1");
        user.setEmail("1test@test.ru");
        entityManager.persist(user);

        itemRequest = new ItemRequest();
        itemRequest.setDescription("Item request description");
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());
        entityManager.persist(itemRequest);

        item = new Item();
        item.setName("TestItem1");
        item.setDescription("DescriptionOfItemTest");
        item.setAvailable(true);
        item.setOwner(user);
        item.setRequestId(itemRequest.getId());
        entityManager.persist(item);

        item2 = new Item();
        item2.setName("TestItem2");
        item2.setDescription("DescriptionOfItemTest2");
        item2.setAvailable(true);
        item2.setOwner(user);
        item2.setRequestId(itemRequest.getId());
        entityManager.persist(item2);
    }

    @AfterEach
    void clear() {
        entityManager.clear();
    }

    @Test
    void whenFindAllByOwnerIdIsSuccess() {
        List<Item> expected = List.of(item, item2);

        List<Item> actual = itemRepository.findAllByOwnerId(user.getId(), pageRequest);

        assertEquals(expected, actual);

    }

    @Test
    void whenFindItemIdsByOwner_IdIsSuccess() {
        List<Long> expected = List.of(item.getId(), item2.getId());

        List<Long> actual = itemRepository.findItemIdsByOwner_Id(user.getId());

        assertEquals(expected, actual);
    }

    @Test
    void whenFindByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCaseAndAvailableTrueIsSuccess() {
        item2.setAvailable(false);
        List<Item> expected = List.of(item);

        List<Item> actual = itemRepository
                .findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(
                        "TestItem",
                        "DescriptionOfItemTest",
                        pageRequest);

        assertEquals(expected, actual);
    }

    @Test
    void whenFindByRequestIdIsSuccess() {
        List<Item> expected = List.of(item, item2);
        List<Item> actual = itemRepository.findAllByRequestId(itemRequest.getId());

        assertEquals(expected, actual);
    }

    @Test
    void whenFindAllByRequestIdInIsSuccess() {
        List<Long> requests = List.of(itemRequest.getId());

        List<Item> expected = List.of(item, item2);
        List<Item> actual = itemRepository.findByRequestIdIn(requests, Sort.by(Sort.Direction.ASC, "id"));

        assertEquals(expected, actual);
    }
}