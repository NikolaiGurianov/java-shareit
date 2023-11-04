package ru.practicum.shareitserver.item.comment;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareitserver.item.comment.dto.CommentRepository;
import ru.practicum.shareitserver.item.comment.model.Comment;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DataJpaTest
public class CommentRepositoryTest {
    private final TestEntityManager entityManager;
    private final CommentRepository commentRepository;

    @Test
    void whenFindByItemIdIsSuccess() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        user.setName("Test1");
        user.setEmail("test@test.ru1");

        Item item = new Item();
        item.setName("TestItem1");
        item.setDescription("DescriptionOfItemTest");
        item.setAvailable(true);
        item.setOwner(user);

        Comment comment = new Comment();
        comment.setText("TextOfCommentTest");
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(now);

        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(comment);

        List<Comment> expected = List.of(comment);

        List<Comment> actual = commentRepository.findByItem_Id(item.getId());

        assertEquals(actual, expected);
    }

}

