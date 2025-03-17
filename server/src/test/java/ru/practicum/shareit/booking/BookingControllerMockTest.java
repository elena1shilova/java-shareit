package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final User USER = User.builder()
            .id(100)
            .name("Test User")
            .email("test@mail.ru")
            .build();
    private static final BookingDto BOOKING_DTO = BookingDto.builder()
            .id(1)
            .start(LocalDateTime.now().plusMinutes(1))
            .end(LocalDateTime.now().plusMinutes(15))
            .itemId(100)
            .booker(USER)
            .status(Status.WAITING)
            .build();
    private static final BookingDto BOOKING_DTO_APPR = BookingDto.builder()
            .id(2)
            .start(LocalDateTime.now().plusMinutes(1))
            .end(LocalDateTime.now().plusMinutes(15))
            .itemId(100)
            .booker(USER)
            .status(Status.APPROVED)
            .build();
    private static final BookingDto BOOKING_DTO_REJ = BookingDto.builder()
            .id(3)
            .start(LocalDateTime.now().plusMinutes(1))
            .end(LocalDateTime.now().plusMinutes(15))
            .itemId(100)
            .booker(USER)
            .status(Status.REJECTED)
            .build();

    private static final List<BookingDto> BOOKING_DTO_LIST = List.of(
            BOOKING_DTO_REJ, BOOKING_DTO, BOOKING_DTO_APPR
    );

    @Test
    void createTest() throws Exception {

        when(bookingService.create(any(BookingDto.class), anyInt())).thenReturn(BOOKING_DTO);

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
    void bookingsApprovedTest() throws Exception {

        when(bookingService.bookingsApproved(anyInt(), anyBoolean(), anyInt())).thenReturn(BOOKING_DTO_APPR);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 100)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.itemId").value(100))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void findByIdBookingTest() throws Exception {

        when(bookingService.findByIdBooking(anyInt(), anyInt())).thenReturn(BOOKING_DTO);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 100))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.itemId").value(100))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void findAllForStateTest() throws Exception {

        when(bookingService.findAllForState(any(State.class), anyInt())).thenReturn(BOOKING_DTO_LIST);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 100)
                        .param("state", "REJECTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].start").isNotEmpty())
                .andExpect(jsonPath("$[0].end").isNotEmpty())
                .andExpect(jsonPath("$[0].itemId").value(100))
                .andExpect(jsonPath("$[0].status").value("REJECTED"));
    }

    @Test
    void findAllOwnerForStateTest() throws Exception {

        when(bookingService.findAllOwnerForState(any(State.class), anyInt())).thenReturn(BOOKING_DTO_LIST);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 100)
                        .param("state", "REJECTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }
}
