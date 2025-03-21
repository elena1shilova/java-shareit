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

    private static final BookingDto BOOKING_DTO = BookingDto.builder()
            .start(LocalDateTime.now().plusMinutes(1))
            .end(LocalDateTime.now().plusMinutes(15))
            .itemId(100)
            .build();

    @Test
    void createTest() throws Exception {

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 100)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(BOOKING_DTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.itemId").value(100))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void findAllForStateTest() throws Exception {

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 100)
                        .param("state", "REJECTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(101))
                .andExpect(jsonPath("$[0].start").isNotEmpty())
                .andExpect(jsonPath("$[0].end").isNotEmpty())
                .andExpect(jsonPath("$[0].itemId").value(100))
                .andExpect(jsonPath("$[0].status").value("REJECTED"));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 100)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 100)
                        .param("state", "FUTURE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 100)
                        .param("state", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void findAllOwnerForStateTest() throws Exception {

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 100)
                        .param("state", "REJECTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(101));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 100)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 100)
                        .param("state", "FUTURE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 100)
                        .param("state", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 100)
                        .param("state", "START"))
                .andExpect(status().isInternalServerError());

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 102)
                        .param("state", "WAITING"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void bookingsApprovedTest() throws Exception {

        mockMvc.perform(patch("/bookings/103")
                        .header("X-Sharer-User-Id", 100)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(103))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.itemId").value(100))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void bookingsApprovedNotFoundTest() throws Exception {

        mockMvc.perform(patch("/bookings/102")
                        .header("X-Sharer-User-Id", 101)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка валидации."));
    }

    @Test
    void findByIdBookingTest() throws Exception {

        mockMvc.perform(get("/bookings/101")
                        .header("X-Sharer-User-Id", 100))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(101))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.itemId").value(100))
                .andExpect(jsonPath("$.status").value("REJECTED"));

        mockMvc.perform(get("/bookings/101")
                        .header("X-Sharer-User-Id", 101))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/bookings/1010")
                        .header("X-Sharer-User-Id", 101))
                .andExpect(status().isNotFound());
    }
}
