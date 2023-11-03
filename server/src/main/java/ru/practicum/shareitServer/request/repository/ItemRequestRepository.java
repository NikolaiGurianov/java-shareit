package ru.practicum.shareitServer.request.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.practicum.shareitServer.request.model.ItemRequest;

import java.util.List;

@Component
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequesterId(Long requesterId, PageRequest pageRequest);

    List<ItemRequest> findAllByRequesterIdNot(Long userId, PageRequest pageRequest);
}
