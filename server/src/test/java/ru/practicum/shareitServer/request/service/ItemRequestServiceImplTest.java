package ru.practicum.shareitServer.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareitServer.exception.NotFoundException;
import ru.practicum.shareitServer.item.dto.ItemDto;
import ru.practicum.shareitServer.item.dto.ItemMapper;
import ru.practicum.shareitServer.item.model.Item;
import ru.practicum.shareitServer.item.repository.ItemRepository;
import ru.practicum.shareitServer.request.dto.ItemRequestDto;
import ru.practicum.shareitServer.request.dto.ItemRequestMapper;
import ru.practicum.shareitServer.request.model.ItemRequest;
import ru.practicum.shareitServer.request.repository.ItemRequestRepository;
import ru.practicum.shareitServer.request.service.ItemRequestServiceImpl;
import ru.practicum.shareitServer.user.model.User;
import ru.practicum.shareitServer.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private final ItemRepository itemRepository;
    @Mock
    private final UserRepository userRepository;
    @Mock
    private final ItemRequestRepository itemRequestRepository;

    private final ItemMapper itemMapper = new ItemMapper();
    private final ItemRequestMapper itemRequestMapper = new ItemRequestMapper();

    private User user;
    private Item item;
    private ItemDto itemDto;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        itemRequestService = new ItemRequestServiceImpl(itemRepository, itemMapper, userRepository,
                itemRequestRepository, itemRequestMapper);

        user = new User();
        user.setId(1L);
        user.setName("test name");
        user.setEmail("test@test.ru");

        item = new Item();
        item.setId(1L);
        item.setName("TestItem1");
        item.setDescription("DescriptionOfItemTest");
        item.setAvailable(true);
        item.setOwner(user);

        itemDto = itemMapper.toItemDto(item);

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Item request description");
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());

        itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest, List.of(), user);
    }

    @Test
    void whenCreateItemRequestIsSuccess() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto expected = itemRequestDto;
        ItemRequestDto actual = itemRequestService.createItemRequest(itemRequestDto, user.getId());

        assertEquals(expected, actual);
    }

    @Test
    void whenCreateItemRequestByInvalidUserIdIsNotSuccess() {
        String expectedMessage = "Пользователь не найден с ID={}";

        assertThatThrownBy(() -> itemRequestService.createItemRequest(itemRequestDto, 999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(expectedMessage);
    }


    @Test
    void whenGetRequestsByOwnerIsSuccess() {
        List<ItemRequest> requestList = new ArrayList<>();
        requestList.add(itemRequest);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterId(any(Long.class), any())).thenReturn(requestList);

        List<ItemRequestDto> expected = List.of(itemRequestDto);
        List<ItemRequestDto> actual = itemRequestService.getRequestsByOwner(user.getId());

        assertEquals(expected, actual);
    }

    @Test
    void whenGetRequestsByInvalidOwnerIsNotSuccess() {
        String expectedMessage = "Пользователь не найден с ID={}";

        assertThatThrownBy(() -> itemRequestService.getRequestsByOwner(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(expectedMessage);
    }

    @Test
    void whenGetAllRequestsIsSuccess() {
        List<ItemRequest> requestList = List.of(itemRequest);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterIdNot(any(Long.class), any())).thenReturn(requestList);

        List<ItemRequestDto> expected = List.of(itemRequestDto);
        List<ItemRequestDto> actual = itemRequestService.getAllRequests(1L, 0, 10);

        assertEquals(expected, actual);
    }

    @Test
    void whenGetAllRequestsWithInvalidUserIsNotSuccess() {
        String expectedMessage = "Пользователь не найден с ID={}";

        assertThatThrownBy(() -> itemRequestService.getAllRequests(999L, 0, 10))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(expectedMessage);
    }

    @Test
    void whenGetRequestByIdIsSuccess() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(any(Long.class))).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(any(Long.class))).thenReturn(List.of(item));

        itemRequestDto.setItems(List.of(itemDto));

        ItemRequestDto expected = itemRequestDto;
        ItemRequestDto actual = itemRequestService.getRequestById(user.getId(), itemRequest.getId());

        assertEquals(expected, actual);
    }

    @Test
    void whenGetRequestByInvalidIdIsNotSuccess() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));

        String expectedMessage = "Запрос не найден с ID={}";

        assertThatThrownBy(() -> itemRequestService.getRequestById(user.getId(), 999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(expectedMessage);
    }
}