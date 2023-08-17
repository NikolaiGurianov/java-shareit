package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ShareItTests {
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @DisplayName("Тест создания нового пользователя")
    @Test
    public void createUserTest() {
        User actualUser = new User(1L, "Name", "email@email.ru");
        userStorage.createUser(actualUser);
        Assertions.assertEquals(actualUser, userStorage.getUserById(1),
                "Ожидался корректный новый пользователь");
    }

    @DisplayName("Тест обновления пользователя")
    @Test
    public void updateUserTest() {
        User actualUser = new User(1L, "Name", "email@email.ru");
        User updatedUser = new User(1L, "UpdatedName", "email@yandex.ru");
        userStorage.createUser(actualUser);
        userStorage.updateUser(updatedUser, actualUser.getId());
        Assertions.assertEquals(updatedUser, userStorage.getUserById(1),
                "Ожидался корректный обновленный пользователь");
    }

    @DisplayName("Тест получения пользователя по ID")
    @Test
    public void getUserByIdTest() {
        User actualUser = new User(1L, "Name", "email@email.ru");
        User updatedUser = new User(1L, "UpdatedName", "email@yandex.ru");
        userStorage.createUser(actualUser);
        userStorage.createUser(updatedUser);
        Assertions.assertEquals(actualUser, userStorage.getUserById(1),
                "Ожидался корректный пользователь");
        Assertions.assertEquals(updatedUser, userStorage.getUserById(2),
                "Ожидался корректный пользователь");
    }

    @DisplayName("Тест получения всех пользователей")
    @Test
    public void getUsersTest() {
        User actualUser = new User(1L, "Name", "email@email.ru");
        User updatedUser = new User(1L, "UpdatedName", "email@yandex.ru");
        userStorage.createUser(actualUser);
        userStorage.createUser(updatedUser);
        Assertions.assertEquals(2, userStorage.getUsers().size(),
                "Ожидалось другое колличество пользователей");
    }

    @DisplayName("Тест удаления пользователя по ID")
    @Test
    public void deleteUserByIdTest() {
        User actualUser = new User(1L, "Name", "email@email.ru");
        User updatedUser = new User(1L, "UpdatedName", "email@yandex.ru");
        userStorage.createUser(actualUser);
        userStorage.createUser(updatedUser);
        Assertions.assertEquals(2, userStorage.getUsers().size(),
                "Ожидалось другое колличество пользователей");
        userStorage.deleteUserById(2);
        Assertions.assertEquals(1, userStorage.getUsers().size(),
                "Ожидалось другое колличество пользователей");
    }

    @DisplayName("Тест создания новой вещи")
    @Test
    public void addItemTest() {
        User actualUser = new User(1L, "Name", "email@email.ru");
        userStorage.createUser(actualUser);
        Item actuaItem = new Item(1L, "Name", "Description", true, 1L, new ItemRequest());
        itemStorage.addItem(actuaItem);
        Assertions.assertEquals(actuaItem, itemStorage.getItemById(1),
                "Ожидалась корректная новая вещь");
    }

    @DisplayName("Тест обновления вещи")
    @Test
    public void updateItemTest() {
        User actualUser = new User(1L, "Name", "email@email.ru");
        userStorage.createUser(actualUser);
        Item actuaItem = new Item(1L, "Name", "Description", true, 1L, new ItemRequest());
        Item updatedItem = new Item(1L, "NewName", "NewDescription", false, 1L, new ItemRequest());
        itemStorage.addItem(actuaItem);
        itemStorage.updateItem(actuaItem.getId(), updatedItem, actuaItem.getOwnerId());
        Assertions.assertEquals(updatedItem, itemStorage.getItemById(1),
                "Ожидалась корректная обновленная вещь");
    }

    @DisplayName("Тест получения вещей пользователя")
    @Test
    public void getItemsByOwnerTest() {
        User actualUser = new User(1L, "Name", "email@email.ru");
        userStorage.createUser(actualUser);
        Item actuaItem = new Item(1L, "Name", "Description", true, 1L, new ItemRequest());
        Item updatedItem = new Item(1L, "NewName", "NewDescription", false, 1L, new ItemRequest());
        itemStorage.addItem(actuaItem);
        itemStorage.addItem(updatedItem);
        Assertions.assertEquals(2, itemStorage.getItemsByOwner(actualUser.getId()).size(),
                "Ожидалось другое колличество вещей");
    }

    @DisplayName("Тест получения вещей по поиску")
    @Test
    public void searchItemsTest() {
        User actualUser = new User(1L, "Name", "email@email.ru");
        userStorage.createUser(actualUser);
        Item actuaItem = new Item(1L, "Дрель", "Description", true, 1L, new ItemRequest());
        Item actualItem2 = new Item(1L, "NewName", "Насадка на дрель", true, 1L, new ItemRequest());
        Item actualItem3 = new Item(1L, "NewName", "Description", true, 1L, new ItemRequest());

        itemStorage.addItem(actuaItem);
        itemStorage.addItem(actualItem2);
        itemStorage.addItem(actualItem3);
        Assertions.assertEquals(2, itemStorage.searchItems("ДрЕЛь").size(),
                "Ожидалось другое колличество вещей");
    }
}
