package ru.practicum.shareitserver.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.practicum.shareitserver.user.model.User;

@Component
public interface UserRepository extends JpaRepository<User, Long> {
}

