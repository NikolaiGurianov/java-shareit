package ru.practicum.shareitServer.item.dto;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareitServer.booking.dto.BookingLastNextDto;
import ru.practicum.shareitServer.booking.model.Booking;
import ru.practicum.shareitServer.item.comment.dto.CommentDto;
import ru.practicum.shareitServer.item.comment.dto.CommentMapper;
import ru.practicum.shareitServer.item.comment.model.Comment;
import ru.practicum.shareitServer.item.model.Item;
import ru.practicum.shareitServer.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Data
public class ItemMapper {
    public Item toItem(ItemDto itemDto, User owner) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(owner);
        item.setRequestId(itemDto.getRequestId());
        return item;
    }

    public ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwnerId(item.getOwnerId());
        itemDto.setRequestId(item.getRequestId());
        return itemDto;
    }

    public ItemLastNextDto toItemLastNextDto(Item item, Booking lastBooking, Booking nextBooking, List<Comment> commentList) {
        BookingLastNextDto last = (lastBooking == null) ? null : new BookingLastNextDto(lastBooking.getId(), lastBooking.getBooker().getId());
        BookingLastNextDto next = (nextBooking == null) ? null : new BookingLastNextDto(nextBooking.getId(), nextBooking.getBooker().getId());
        List<CommentDto> comments = commentList
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        ItemLastNextDto itemLastNextDto = new ItemLastNextDto();
        itemLastNextDto.setId(item.getId());
        itemLastNextDto.setName(item.getName());
        itemLastNextDto.setDescription(item.getDescription());
        itemLastNextDto.setAvailable(item.getAvailable());
        itemLastNextDto.setLastBooking(last);
        itemLastNextDto.setNextBooking(next);
        itemLastNextDto.setComments(comments);
        return itemLastNextDto;
    }
}
