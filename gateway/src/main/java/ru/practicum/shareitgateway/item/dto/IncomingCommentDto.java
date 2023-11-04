package ru.practicum.shareitgateway.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncomingCommentDto {
    private long id;
    @NotBlank(message = "Текст комментария не может быть пустым")
    private String text;
    private String authorName;
    private LocalDateTime created;
}