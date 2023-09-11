package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Column(name = "name", nullable = false)
    private String name;
    @Email(message = "Неверный формат электронной почты")
    @NotBlank(message = "Электронная почта не может быть пустой")
    @Column(name = "email", nullable = false, unique = true)
    private String email;
}
