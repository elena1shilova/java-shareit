package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/request/data.sql")
public class RequestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTest() throws Exception {

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Test Description");
        itemRequestDto.setRequestorId(100);
        itemRequestDto.setCreated(LocalDateTime.now());

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 10)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.requestorId").value(10))
                .andExpect(jsonPath("$.created").isNotEmpty());

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1000)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllByUserIdTest() throws Exception {

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 11))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(101))
                .andExpect(jsonPath("$[0].description").value("requestors2"))
                .andExpect(jsonPath("$[0].requestorId").value(11))
                .andExpect(jsonPath("$[0].created").isNotEmpty());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 13))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllByNotUserIdTest() throws Exception {

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 10))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void getByRequestIdTest() throws Exception {

        mockMvc.perform(get("/requests/103"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(103))
                .andExpect(jsonPath("$.description").value("requestors4"))
                .andExpect(jsonPath("$.requestorId").value(12))
                .andExpect(jsonPath("$.created").isNotEmpty());

        mockMvc.perform(get("/requests/10300"))
                .andExpect(status().isNotFound());
    }
}
