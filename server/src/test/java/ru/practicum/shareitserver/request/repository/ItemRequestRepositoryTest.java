package ru.practicum.shareitserver.request.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareitserver.request.model.ItemRequest;
import ru.practicum.shareitserver.user.model.User;
import ru.practicum.shareitserver.util.Constant;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DataJpaTest
class ItemRequestRepositoryTest {
    private final TestEntityManager entityManager;
    private final ItemRequestRepository itemRequestRepository;

    private final User user1 = new User(null, "Test1", "test@test.ru1");
    private final User user2 = new User(null, "Test2", "test@test.ru2");

    private ItemRequest request1;
    private ItemRequest request2;
    private ItemRequest request3;


    @BeforeEach
    void setUp() {
        request1 = new ItemRequest();
        request1.setDescription("Item request description 1");
        request1.setRequester(user1);
        request1.setCreated(LocalDateTime.now().plusDays(2));

        request2 = new ItemRequest();
        request2.setDescription("Item request description 2");
        request2.setRequester(user1);
        request2.setCreated(LocalDateTime.now().plusDays(4));

        request3 = new ItemRequest();
        request3.setDescription("Item request description 3");
        request3.setRequester(user2);
        request3.setCreated(LocalDateTime.now().plusDays(6));

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.persist(request3);
    }

    @AfterEach
    void clear() {
        entityManager.clear();
    }

    @Test
    void whenFindAllByRequesterIdOrderByCreatedDescIsSuccess() {
        List<ItemRequest> expected = List.of(request2, request1);

        List<ItemRequest> actual = itemRequestRepository.findAllByRequesterId(user1.getId(),
                PageRequest.of(0, 3, Constant.SORT_BY_DESC_CREATED));

        assertEquals(expected, actual);
    }

    @Test
    void whenFindAllByRequesterIdNotOrderByCreatedDescIsSuccess() {
        List<ItemRequest> expected = List.of(request3);

        List<ItemRequest> actual = itemRequestRepository
                .findAllByRequesterIdNot(user1.getId(),
                        PageRequest.of(0, 3, Constant.SORT_BY_DESC_CREATED));

        assertEquals(expected, actual);
    }
}