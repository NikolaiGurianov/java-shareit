//package ru.practicum.shareit;
//
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.DirtiesContext;
//import ru.practicum.shareit.booking.dto.BookingDto;
//import ru.practicum.shareit.booking.dto.IncomingBookingDto;
//import ru.practicum.shareit.booking.model.Status;
//import ru.practicum.shareit.booking.service.BookingService;
//import ru.practicum.shareit.item.dto.ItemDto;
//import ru.practicum.shareit.item.dto.ItemLastNextDto;
//import ru.practicum.shareit.item.dto.ItemMapper;
//import ru.practicum.shareit.item.service.ItemService;
//import ru.practicum.shareit.user.dto.UserDto;
//import ru.practicum.shareit.user.service.UserService;
//
//import java.time.LocalDateTime;
//
//@SpringBootTest
//@AutoConfigureTestDatabase
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
//class ShareItTests {
//    private final UserService userService;
//    private final ItemService itemService;
//    private final ItemMapper itemMapper;
//    private final BookingService bookingService;
//
//    @DisplayName("Тест создания нового пользователя")
//    @Test
//    public void createUserTest() {
//        UserDto actualUser = new UserDto(1L, "Name", "email@email.ru");
//        userService.createUser(actualUser);
//        Assertions.assertEquals(actualUser, userService.getUserById(1L),
//                "Ожидался корректный новый пользователь");
//    }
//
//    @DisplayName("Тест обновления пользователя")
//    @Test
//    public void updateUserTest() {
//        UserDto actualUser = new UserDto(1L, "Name", "email@email.ru");
//        UserDto updatedUser = new UserDto(1L, "UpdatedName", "email@yandex.ru");
//        userService.createUser(actualUser);
//        userService.updateUser(updatedUser, actualUser.getId());
//        Assertions.assertEquals(updatedUser, userService.getUserById(1L),
//                "Ожидался корректный обновленный пользователь");
//    }
//
//    @DisplayName("Тест получения пользователя по ID")
//    @Test
//    public void getUserByIdTest() {
//        UserDto actualUser = new UserDto(1L, "Name", "email@email.ru");
//        UserDto updatedUser = new UserDto(2L, "UpdatedName", "email@yandex.ru");
//        userService.createUser(actualUser);
//        userService.createUser(updatedUser);
//        Assertions.assertEquals(actualUser, userService.getUserById(1L),
//                "Ожидался корректный пользователь");
//        Assertions.assertEquals(updatedUser, userService.getUserById(2L),
//                "Ожидался корректный пользователь");
//    }
//
//    @DisplayName("Тест получения всех пользователей")
//    @Test
//    public void getUsersTest() {
//        UserDto actualUser = new UserDto(1L, "Name", "email@email.ru");
//        UserDto updatedUser = new UserDto(1L, "UpdatedName", "email@yandex.ru");
//        userService.createUser(actualUser);
//        userService.createUser(updatedUser);
//        Assertions.assertEquals(2, userService.getUsers().size(),
//                "Ожидалось другое колличество пользователей");
//    }
//
//    @DisplayName("Тест удаления пользователя по ID")
//    @Test
//    public void deleteUserByIdTest() {
//        UserDto actualUser = new UserDto(1L, "Name", "email@email.ru");
//        UserDto updatedUser = new UserDto(1L, "UpdatedName", "email@yandex.ru");
//        userService.createUser(actualUser);
//        userService.createUser(updatedUser);
//        Assertions.assertEquals(2, userService.getUsers().size(),
//                "Ожидалось другое колличество пользователей");
//        userService.deleteUserById(2);
//        Assertions.assertEquals(1, userService.getUsers().size(),
//                "Ожидалось другое колличество пользователей");
//    }
//
//    @DisplayName("Тест создания новой вещи")
//    @Test
//    public void addItemTest() {
//        UserDto actualUser = new UserDto(1L, "Name", "email@email.ru");
//        userService.createUser(actualUser);
//        ItemDto actualItem = new ItemDto(1L, "Name", "Description", true, 1L, 1L);
//        itemService.addItem(actualItem, actualUser.getId());
//
//        ItemLastNextDto itm = itemService.getItemById(1L, actualUser.getId());
//        Assertions.assertEquals(actualItem.getName(), itm.getName(),
//                "Ожидалась корректное название вещи");
//        Assertions.assertEquals(actualItem.getDescription(), itm.getDescription(),
//                "Ожидалась корректное описание вещи");
//    }
//
//    @DisplayName("Тест обновления вещи")
//    @Test
//    public void updateItemTest() {
//        UserDto actualUser = new UserDto(1L, "Name", "email@email.ru");
//        userService.createUser(actualUser);
//        ItemDto actuaItem = new ItemDto(1L, "Name", "Description", true, 1L, 1L);
//        ItemDto updatedItem = new ItemDto(1L, "NewName", "NewDescription", false, 1L, 1L);
//        itemService.addItem(actuaItem, actualUser.getId());
//        ItemDto itm = itemService.updateItem(actuaItem.getId(), updatedItem, actuaItem.getOwnerId());
//        Assertions.assertEquals(updatedItem.getName(), itm.getName(),
//                "Ожидалась корректное название вещи");
//        Assertions.assertEquals(updatedItem.getDescription(), itm.getDescription(),
//                "Ожидалась корректное описание вещи");
//    }
//
//    @DisplayName("Тест получения вещей пользователя")
//    @Test
//    public void getItemsByOwnerTest() {
//        UserDto actualUser = new UserDto(1L, "Name", "email@email.ru");
//        userService.createUser(actualUser);
//        ItemDto actuaItem = new ItemDto(1L, "Name", "Description", true, 1L, 1L);
//        ItemDto updatedItem = new ItemDto(1L, "NewName", "NewDescription", false, 1L, 1L);
//        itemService.addItem(actuaItem, actualUser.getId());
//        itemService.addItem(updatedItem, actualUser.getId());
//        Assertions.assertEquals(2, itemService.getItemsByOwner(actualUser.getId()).size(),
//                "Ожидалось другое колличество вещей");
//    }
//
//    @DisplayName("Тест получения вещей по поиску")
//    @Test
//    public void searchItemsTest() {
//        UserDto actualUser = new UserDto(1L, "Name", "email@email.ru");
//        userService.createUser(actualUser);
//        ItemDto actuaItem = new ItemDto(1L, "Дрель", "Description", true, 1L, 1L);
//        ItemDto actualItem2 = new ItemDto(2L, "Name", "Насадка на дрель", true, 1L, 1L);
//        ItemDto actualItem3 = new ItemDto(3L, "NewName", "Description", true, 1L, 1L);
//
//        itemService.addItem(actuaItem, actualUser.getId());
//        itemService.addItem(actualItem2, actualUser.getId());
//        itemService.addItem(actualItem3, actualUser.getId());
//
//        Assertions.assertEquals(2, itemService.searchItems("ДрЕЛь").size(),
//                "Ожидалось другое колличество вещей");
//    }
//
//    @DisplayName("Тест бронирования вещи")
//    @Test
//    public void addBookingTest() {
//        UserDto actualUser = new UserDto(1L, "Name", "email@email.ru");
//        userService.createUser(actualUser);
//        UserDto booker = new UserDto(2L, "Name1", "emailw@email.ru");
//        userService.createUser(booker);
//        ItemDto actuaItem = new ItemDto(1L, "Дрель", "Description", true, 1L, 1L);
//        itemService.addItem(actuaItem, actualUser.getId());
//        LocalDateTime now = LocalDateTime.now();
//        IncomingBookingDto incomingBookingDto =
//                new IncomingBookingDto(now.plusHours(2), now.plusHours(4), 1, 1, Status.WAITING);
//        BookingDto bookingDto = bookingService.addBooking(incomingBookingDto, booker.getId());
//
//        Assertions.assertEquals(bookingDto.getItem().getName(), actuaItem.getName(),
//                "Ожидалось другое название бронированной вещи");
//    }
//
//}
