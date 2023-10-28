package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentMapperTest {
    @Test
    public void whenToCommentIsSuccess() {
        CommentDto commentDto = new CommentDto();
        Item item = new Item();
        User user = new User();

        commentDto.setText("Test Comment Text");

        Comment comment = CommentMapper.toComment(commentDto, item, user);

        assertEquals(commentDto.getText(), comment.getText());
        assertEquals(item, comment.getItem());
        assertEquals(user, comment.getAuthor());
    }

    @Test
    public void whenToCommentDtoIsSuccess() {
        Comment comment = new Comment(1, "text", new Item(), new User(), LocalDateTime.now());

        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());
    }
}
