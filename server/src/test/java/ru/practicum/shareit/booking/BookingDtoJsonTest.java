package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testSerialize() throws Exception {
        var dto = new BookingDto();
        dto.setId(1);
        dto.setItemId(2);
        dto.setStart(LocalDateTime.now());
        dto.setEnd(LocalDateTime.now());
        dto.setItem(new Item());
        dto.setBooker(new User());
        dto.setStatus(Status.APPROVED);

        var result = json.write(dto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).hasJsonPath("$.item");
        assertThat(result).hasJsonPath("$.booker");
        assertThat(result).hasJsonPath("$.status");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId());
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(dto.getItemId());
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(dto.getItem().getId());
        assertThat(result).extractingJsonPathStringValue("$.booker.id").isEqualTo(dto.getBooker().getId());
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo(dto.getStatus().toString());
        assertThat(result).hasJsonPathValue("$.start");
        assertThat(result).hasJsonPathValue("$.end");
    }
}
