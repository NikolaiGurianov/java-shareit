package ru.practicum.shareitserver.item.comment.dto;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareitserver.item.comment.model.Comment;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.item.service.ItemService;
import ru.practicum.shareitserver.user.model.User;

import java.time.LocalDateTime;

@Component
@Data
public class CommentMapper {
    private ItemService itemService;

    public static Comment toComment(CommentDto commentDto, Item item, User user) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public static CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

}
