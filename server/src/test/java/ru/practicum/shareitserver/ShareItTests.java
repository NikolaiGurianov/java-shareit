package ru.practicum.shareitserver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.request.model.ItemRequest;
import ru.practicum.shareitserver.user.model.User;

import java.time.LocalDateTime;

@SpringBootTest
class ShareItTests {

    @BeforeEach
    void setUp() {
        User requestor = new User(1L, "Name Requestor", "email@yandex.ru");
        ItemRequest request = new ItemRequest(1L, "description", requestor, LocalDateTime.now());

        Item item1 = new Item(1L, "item1", "item1 description", true, requestor, request.getId());

        User booker = new User(1L, "Name", "email@mail.ru");
    }

    @Test
    public void contextLoads() {
        ShareItServer.main(new String[]{});
    }
}