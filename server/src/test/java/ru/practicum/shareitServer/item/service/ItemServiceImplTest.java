package ru.practicum.shareitServer.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareitServer.booking.model.Booking;
import ru.practicum.shareitServer.booking.model.Status;
import ru.practicum.shareitServer.booking.repository.BookingRepository;
import ru.practicum.shareitServer.exception.ErrorException;
import ru.practicum.shareitServer.exception.NotFoundException;
import ru.practicum.shareitServer.item.comment.dto.CommentDto;
import ru.practicum.shareitServer.item.comment.dto.CommentMapper;
import ru.practicum.shareitServer.item.comment.dto.CommentRepository;
import ru.practicum.shareitServer.item.comment.model.Comment;
import ru.practicum.shareitServer.item.dto.ItemDto;
import ru.practicum.shareitServer.item.dto.ItemLastNextDto;
import ru.practicum.shareitServer.item.dto.ItemMapper;
import ru.practicum.shareitServer.item.model.Item;
import ru.practicum.shareitServer.item.repository.ItemRepository;
import ru.practicum.shareitServer.item.service.ItemServiceImpl;
import ru.practicum.shareitServer.user.model.User;
import ru.practicum.shareitServer.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private final ItemRepository itemRepository;
    @Mock
    private final UserRepository userRepository;
    @Mock
    private final BookingRepository bookingRepository;
    @Mock
    private final CommentRepository commentRepository;

    private final ItemMapper itemMapper = new ItemMapper();
    private final CommentMapper commentMapper = new CommentMapper();

    private Item item;
    private ItemDto itemDto1;
    private User user1;
    private Booking booking1;
    private Booking booking2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository,
                commentRepository, itemMapper, commentMapper);

        user1 = new User();
        user1.setId(1L);
        user1.setName("UserTestName1");
        user1.setEmail("test@test.ru1");

        item = new Item();
        item.setId(1L);
        item.setName("TestItem1TeXt");
        item.setDescription("DescriptionOfItemTest");
        item.setAvailable(true);
        item.setOwner(user1);

        itemDto1 = itemMapper.toItemDto(item);

        booking1 = new Booking();
        booking1.setStart(LocalDateTime.now());
        booking1.setEnd(LocalDateTime.now().plusDays(2));
        booking1.setItem(item);
        booking1.setBooker(new User(2L, "UserTestName2", "test@test.ru2"));
        booking1.setStatus(Status.WAITING);
    }

    @Test
    void whenCreateItemIsSuccess() {
        ItemDto expected = itemMapper.toItemDto(item);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto actual = itemService.addItem(itemDto1, user1.getId());
        assertEquals(actual, expected);
    }

    @Test
    void whenCreateItemByInvalidOwnerIdIsNotSuccess() {
        String expectedMessage = "Пользователь не найден с ID={}";

        assertThatThrownBy(() -> itemService.addItem(itemDto1, 999L))
                .isInstanceOf(NotFoundException.class)
                .message()
                .isEqualTo(expectedMessage);
    }

    @Test
    void whenUpdateItemIsSuccess() {
        Item itemForUpdate = new Item();
        itemForUpdate.setId(1L);
        itemForUpdate.setOwner(user1);
        itemForUpdate.setName("Update Name");
        itemForUpdate.setDescription("Update description");
        itemForUpdate.setAvailable(true);

        ItemDto expected = itemMapper.toItemDto(itemForUpdate);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(itemForUpdate);

        ItemDto actual = itemService.updateItem(item.getId(), itemMapper.toItemDto(itemForUpdate), user1.getId());

        assertEquals(actual, expected);
    }

    @Test
    void whenUpdateItemByInvalidOwnerIdIsNotSuccess() {
        String expectedMessage = "Пользователь не является владельцем данной вещи";

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemService.updateItem(item.getId(), itemDto1, 999L))
                .isInstanceOf(ErrorException.class)
                .message()
                .isEqualTo(expectedMessage);
    }

    @Test
    void whenGetItemByIdIsSuccess() {
        ItemLastNextDto expected = itemMapper.toItemLastNextDto(item, booking1, booking2, new ArrayList<>());

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findLast(anyLong(), any())).thenReturn(booking1);
        when(bookingRepository.findNext(anyLong(), any())).thenReturn(booking2);

        ItemLastNextDto actual = itemService.getItemById(user1.getId(), item.getId());

        assertEquals(expected, actual);
    }

    @Test
    void whenGetItemByInvalidIdIsNotSuccess() {
        String expectedMessage = "Вещь не найдена с ID={}";

        assertThatThrownBy(() -> itemService.getItemById(999L, user1.getId()))
                .isInstanceOf(NotFoundException.class)
                .message()
                .isEqualTo(expectedMessage);
    }

    @Test
    void whenGetItemsByUserIdIsSuccess() {
        ItemLastNextDto itemDto = itemMapper.toItemLastNextDto(item, booking1, booking2, new ArrayList<>());
        List<ItemLastNextDto> expected = List.of(itemDto);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findAllByOwnerId(anyLong(), any())).thenReturn(List.of(item));
        when(bookingRepository.findLast(anyLong(), any())).thenReturn(booking1);
        when(bookingRepository.findNext(anyLong(), any())).thenReturn(booking2);

        List<ItemLastNextDto> actual = itemService.getItemsByOwner(user1.getId(), 0, 3);

        assertEquals(expected, actual);
    }

    @Test
    void whenGetItemsByInvalidUserIdIsNotSuccess() {
        String expectedMessage = "Пользователь не найден с ID={}";

        assertThatThrownBy(() -> itemService.getItemsByOwner(999L, 0, 2))
                .isInstanceOf(NotFoundException.class)
                .message()
                .isEqualTo(expectedMessage);
    }

    @Test
    void whenSearchItemsByTextIsSuccess() {
        List<ItemDto> expected = List.of(itemDto1);
        when(itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(any(), any(), any()))
                .thenReturn(List.of(item));
        List<ItemDto> actual = itemService.searchItems("text", 0, 3);

        assertEquals(expected, actual);
    }

    @Test
    void whenAddCommentIsSuccess() {
        LocalDateTime now = LocalDateTime.now();
        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("text");
        comment.setAuthor(user1);
        comment.setCreated(now);

        CommentDto expected = new CommentDto();
        expected.setId(1);
        expected.setText("text");
        expected.setCreated(now);
        expected.setAuthorName(user1.getName());

        when(bookingRepository.existsByBooker_IdAndEndBeforeAndStatus(anyLong(), any(), any())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDto actual = itemService.addComment(user1.getId(), CommentMapper.toCommentDto(comment), item.getId());

        assertEquals(expected, actual);
    }

    @Test
    void whenAddCommentByInvalidItemIdIsNotSuccess() {
        String expectedMessage = "Вещь не найдена с ID={}";
        LocalDateTime now = LocalDateTime.now();
        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("text");
        comment.setAuthor(user1);
        comment.setCreated(now);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        assertThatThrownBy(() -> itemService.addComment(user1.getId(), CommentMapper.toCommentDto(comment), 999L))
                .isInstanceOf(NotFoundException.class)
                .message()
                .isEqualTo(expectedMessage);
    }
}
