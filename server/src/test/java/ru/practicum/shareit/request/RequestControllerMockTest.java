package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class RequestControllerMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final ItemRequestDto REQ_DTO = new ItemRequestDto(1, "Test Description",
            100, LocalDateTime.now(), List.of(new Item()));

    private static final List<ItemRequestDto> REQ_DTO_LIST = List.of(REQ_DTO);

    @Test
    void createTest() throws Exception {

        when(itemRequestService.create(any(ItemRequestDto.class), anyInt())).thenReturn(REQ_DTO);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 100)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(REQ_DTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.requestorId").value(100))
                .andExpect(jsonPath("$.created").isNotEmpty());
    }

    @Test
    void getAllByUserIdTest() throws Exception {

        when(itemRequestService.getAllByUserId(anyInt())).thenReturn(REQ_DTO_LIST);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 100))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Test Description"))
                .andExpect(jsonPath("$[0].requestorId").value(100))
                .andExpect(jsonPath("$[0].created").isNotEmpty());
    }

    @Test
    void getAllByNotUserIdTest() throws Exception {

        when(itemRequestService.getAllByNotUserId(anyInt())).thenReturn(REQ_DTO_LIST);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 101))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getByRequestIdTest() throws Exception {

        when(itemRequestService.getByRequestId(anyInt())).thenReturn(REQ_DTO);

        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.requestorId").value(100))
                .andExpect(jsonPath("$.created").isNotEmpty());
    }
}
