package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookedItemDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingControllerTest {
    @MockBean
    BookingService bookingService;

    private MockMvc mvc;
    private ObjectMapper mapper;

    private BookingDto bookingDto;
    private IncomingBookingDto incomingBookingDto;
    private final State state = State.CURRENT;
    private final Long userId = 1L;
    private final Long bookingId = 1L;
    private final Integer from = 0;
    private final Integer size = 20;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new BookingController(bookingService)).build();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        bookingDto = new BookingDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                new BookedItemDto(1L, "Text"),
                new BookingDto.BookerDto(userId),
                Status.WAITING);

        incomingBookingDto = new IncomingBookingDto(
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                1L,
                userId,
                Status.WAITING);
    }

    @Test
    void addBooking_ShouldReturnBookingDto() throws Exception {
        when(bookingService.addBooking(any(IncomingBookingDto.class), anyLong())).thenReturn(bookingDto);

        incomingBookingDto.setItemId(1L);
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(incomingBookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name()), String.class));

        verify(bookingService, times(1)).addBooking(incomingBookingDto, userId);
    }

    @Test
    void updateBooking_ShouldReturnBookingDto() throws Exception {
        Long ownerId = 2L;

        when(bookingService.updateBooking(any(IncomingBookingDto.class), anyLong(), anyLong())).thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", ownerId)
                        .content(mapper.writeValueAsString(incomingBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name()), String.class));

        verify(bookingService, times(1)).updateBooking(incomingBookingDto,
                bookingId, ownerId);
    }


    @Test
    void getBookingById_ShouldReturnBookingDto() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name()), String.class));

        verify(bookingService, times(1)).getBookingById(bookingId, userId);
    }

    @Test
    void findBookingByBooker_ShouldReturnListOfBookingDto() throws Exception {
        List<BookingDto> bookingDtos = List.of(bookingDto);
        when(bookingService.getBookingsByBooker(anyLong(), any(State.class), anyInt(), anyInt())).thenReturn(bookingDtos);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state.name())
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDtos)));

        verify(bookingService, times(1)).getBookingsByBooker(userId, state, from, size);
    }

    @Test
    void findBookingByOwner_ShouldReturnListOfBookingDto() throws Exception {
        List<BookingDto> bookingDtos = List.of(bookingDto);
        when(bookingService.getBookingsByOwner(anyLong(), any(State.class), anyInt(), anyInt())).thenReturn(bookingDtos);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state.name())
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDtos)));

        verify(bookingService, times(1)).getBookingsByOwner(userId, state, from, size);
    }
}
