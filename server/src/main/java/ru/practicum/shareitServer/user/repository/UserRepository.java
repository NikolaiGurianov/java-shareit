package ru.practicum.shareitServer.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.practicum.shareitServer.user.model.User;

@Component
public interface UserRepository extends JpaRepository<User, Long> {
}

