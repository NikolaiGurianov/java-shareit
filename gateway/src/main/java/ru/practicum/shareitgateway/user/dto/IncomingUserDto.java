package ru.practicum.shareitgateway.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class IncomingUserDto {
    private Long id;
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String name;
    @Email(message = "Неверный формат электронной почты")
    @NotBlank(message = "Электронная почта не может быть пустой")
    private String email;
}
