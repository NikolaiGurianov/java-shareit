package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

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
        ShareItApp.main(new String[]{}); // Запуск приложения через метод main
        // Также можно проверить какие-либо другие ожидаемые эффекты, если такие есть
    }
}
