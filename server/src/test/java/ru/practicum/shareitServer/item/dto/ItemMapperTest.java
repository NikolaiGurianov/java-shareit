package ru.practicum.shareitServer.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareitServer.booking.dto.BookingLastNextDto;
import ru.practicum.shareitServer.booking.model.Booking;
import ru.practicum.shareitServer.item.comment.dto.CommentDto;
import ru.practicum.shareitServer.item.comment.model.Comment;
import ru.practicum.shareitServer.item.dto.ItemDto;
import ru.practicum.shareitServer.item.dto.ItemLastNextDto;
import ru.practicum.shareitServer.item.dto.ItemMapper;
import ru.practicum.shareitServer.item.model.Item;
import ru.practicum.shareitServer.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemMapperTest {

    @Test
    public void toItemTest() {
        ItemMapper itemMapper = new ItemMapper();
        ItemDto itemDto = new ItemDto();
        itemDto.setName("TestItem");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);

        User owner = new User(1L, "OwnerName", "owner@example.com");

        Item item = itemMapper.toItem(itemDto, owner);

        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(item.getRequestId(), itemDto.getRequestId());
        assertEquals(item.getOwner(), owner);
    }

    @Test
    public void toItemDtoTest() {
        ItemMapper itemMapper = new ItemMapper();
        Item item = new Item();
        item.setId(1L);
        item.setName("TestItem");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(new User(1L, "W", "email@email.ru"));
        item.setRequestId(3L);

        ItemDto itemDto = itemMapper.toItemDto(item);

        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
        assertEquals(itemDto.getOwnerId(), item.getOwnerId());
        assertEquals(itemDto.getRequestId(), item.getRequestId());
    }

    @Test
    public void toItemLastNextDtoTest() {
        ItemMapper itemMapper = new ItemMapper();

        Item item = new Item();
        item.setId(1L);
        item.setName("TestItem");
        item.setDescription("Description");
        item.setAvailable(true);

        Booking lastBooking = new Booking();
        lastBooking.setId(2L);
        lastBooking.setBooker(new User(3L, "Booker", "booker@example.com"));

        Booking nextBooking = new Booking();
        nextBooking.setId(4L);
        nextBooking.setBooker(new User(5L, "NextBooker", "nextbooker@example.com"));

        List<Comment> commentList = new ArrayList<>();
        Comment comment1 = new Comment();
        comment1.setId(6L);
        comment1.setText("TestComment1");
        comment1.setAuthor(new User(7L, "CommentAuthor1", "author1@example.com")); // Заполните данные автора комментария

        Comment comment2 = new Comment();
        comment2.setId(7L);
        comment2.setText("TestComment2");
        comment2.setAuthor(new User(8L, "CommentAuthor2", "author2@example.com")); // Заполните данные автора комментария

        commentList.add(comment1);
        commentList.add(comment2);

        BookingLastNextDto lastBookingDto = new BookingLastNextDto(lastBooking.getId(), lastBooking.getBooker().getId());
        BookingLastNextDto nextBookingDto = new BookingLastNextDto(nextBooking.getId(), nextBooking.getBooker().getId());

        CommentDto commentDto1 = new CommentDto();
        commentDto1.setId(comment1.getId());
        commentDto1.setText(comment1.getText());
        commentDto1.setAuthorName(comment1.getAuthor().getName());
        commentDto1.setCreated(comment1.getCreated());

        CommentDto commentDto2 = new CommentDto();
        commentDto2.setId(comment2.getId());
        commentDto2.setText(comment2.getText());
        commentDto2.setAuthorName(comment2.getAuthor().getName());
        commentDto2.setCreated(comment2.getCreated());

        List<CommentDto> commentDtoList = new ArrayList<>();
        commentDtoList.add(commentDto1);
        commentDtoList.add(commentDto2);

        ItemLastNextDto itemLastNextDto = itemMapper.toItemLastNextDto(item, lastBooking, nextBooking, commentList);

        assertEquals(itemLastNextDto.getId(), item.getId());
        assertEquals(itemLastNextDto.getName(), item.getName());
        assertEquals(itemLastNextDto.getDescription(), item.getDescription());
        assertEquals(itemLastNextDto.isAvailable(), item.getAvailable());
        assertEquals(itemLastNextDto.getLastBooking(), lastBookingDto);
        assertEquals(itemLastNextDto.getNextBooking(), nextBookingDto);
        assertEquals(itemLastNextDto.getComments(), commentDtoList);
    }
}