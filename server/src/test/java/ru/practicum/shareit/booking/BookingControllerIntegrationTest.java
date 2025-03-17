package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/booking/data.sql")
public class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTest() throws Exception {

        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusMinutes(1));
        bookingDto.setEnd(LocalDateTime.now().plusMinutes(15));
        bookingDto.setItemId(100);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 100)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.itemId").value(100))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void findAllForStateTest() throws Exception {

        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusMinutes(1));
        bookingDto.setEnd(LocalDateTime.now().plusMinutes(15));
        bookingDto.setItemId(100);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 100)
                        .param("state", "REJECTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(101))
                .andExpect(jsonPath("$[0].start").isNotEmpty())
                .andExpect(jsonPath("$[0].end").isNotEmpty())
                .andExpect(jsonPath("$[0].itemId").value(100))
                .andExpect(jsonPath("$[0].status").value("REJECTED"));
    }

    @Test
    void findAllOwnerForStateTest() throws Exception {

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 100)
                        .param("state", "REJECTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(101));
    }

    @Test
    void bookingsApprovedNotFoundTest() throws Exception {

        mockMvc.perform(patch("/bookings/102")
                        .header("X-Sharer-User-Id", 101)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка валидации."));
    }
}
