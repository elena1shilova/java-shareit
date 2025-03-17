package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/item/data.sql")
public class ItemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTest() throws Exception {

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.owner.id").value(1));
    }

    @Test
    void updateTest() throws Exception {

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item New");
        itemDto.setDescription("Test Description New");
        itemDto.setAvailable(true);

        mockMvc.perform(patch("/items/101")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(101))
                .andExpect(jsonPath("$.name").value("Test Item New"))
                .andExpect(jsonPath("$.description").value("Test Description New"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void getAllTest() throws Exception {

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].name").value("Test Item"))
                .andExpect(jsonPath("$[0].description").value("Test Description"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[0].owner.id").value(1))
                .andExpect(jsonPath("$[0].requestId").value(123));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].lastBooking").isNotEmpty())
                .andExpect(jsonPath("$[0].nextBooking").isNotEmpty());
    }

    @Test
    void searchTextTest() throws Exception {

        mockMvc.perform(get("/items/search")
                        .param("text", "WQWQ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].name").value("WQWQ"))
                .andExpect(jsonPath("$[0].description").value("GFGFG"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[0].owner.id").value(1))
                .andExpect(jsonPath("$[0].requestId").value(123));
    }

    @Test
    public void testGetItemByIdTest() throws Exception {

        mockMvc.perform(get("/items/102"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(102))
                .andExpect(jsonPath("$.name").value("WQWQ"));
    }

    @Test
    @Transactional
    public void testCreateCommentTest() throws Exception {

        mockMvc.perform(post("/items/103/comment")
                        .header("X-Sharer-User-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("text", "Great item!"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Great item!"));
    }

    @Test
    @Transactional
    public void testCreateCommentExceptionTest() throws Exception {

        mockMvc.perform(post("/items/103/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("text", "Great item!"))))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/items/102/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("text", "Great item!"))))
                .andExpect(status().isNotFound());

    }
}
