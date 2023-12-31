package ru.practicum.shareit.item.comment.dto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.comment.model.Comment;

import java.util.List;

@Component
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItem_Id(Long itemId);
}
