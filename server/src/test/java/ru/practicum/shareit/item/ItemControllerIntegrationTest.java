package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/item/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ItemControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemService itemService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllItems_ShouldReturnListOfItemDto() throws Exception {

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].name").value("Test Item"))
                .andExpect(jsonPath("$[0].description").value("Test Description"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[0].owner.id").value(1))
                .andExpect(jsonPath("$[0].requestId").value(123));
    }

    @Test
    void createItem_ShouldReturnCreatedItemDto() throws Exception {

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.owner.id").value(1));
    }

    @Test
    void updateItem_ShouldReturnUpdateItemDto() throws Exception {

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item New");
        itemDto.setDescription("Test Description New");
        itemDto.setAvailable(true);

        ItemDto itemDtoOld = itemService.getAll(1).getFirst();

        mockMvc.perform(patch("/items/" + itemDtoOld.getId())
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDtoOld.getId()))
                .andExpect(jsonPath("$.name").value("Test Item New"))
                .andExpect(jsonPath("$.description").value("Test Description New"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void getSearchItems_ShouldReturnListOfText() throws Exception {

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
}
