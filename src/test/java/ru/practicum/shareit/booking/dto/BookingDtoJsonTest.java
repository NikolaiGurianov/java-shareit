package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoJsonTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void bookingCreationDtoTest() throws Exception {
        var booker = new BookingDto.BookerDto(2L);
        var item = new BookedItemDto(3L, "Item");
        var dto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now(), item, booker, Status.WAITING);

        var result = json.write(dto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.booker");
        assertThat(result).hasJsonPath("$.item");
        assertThat(result).hasJsonPath("$.status");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).hasJsonPathValue("$.start");
        assertThat(result).hasJsonPathValue("$.end");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(booker.getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(item.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo(item.getName());
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(dto.getStatus().toString());
    }
}
