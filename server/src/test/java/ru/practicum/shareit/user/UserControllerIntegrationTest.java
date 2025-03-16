package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ElementNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/user/data.sql")
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTest() throws Exception {

        UserDto userDto = new UserDto();
        userDto.setName("QWER");
        userDto.setEmail("QWER@hotmail.com");

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("QWER"))
                .andExpect(jsonPath("$.email").value("QWER@hotmail.com"));
    }

    @Test
    void updateTest() throws Exception {

        UserDto userDto = new UserDto();
        userDto.setName("QAZWSX");
        userDto.setEmail("QAZWSX@hotmail.com");

        mockMvc.perform(patch("/users/101")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(101))
                .andExpect(jsonPath("$.name").value("QAZWSX"))
                .andExpect(jsonPath("$.email").value("QAZWSX@hotmail.com"));
    }

    @Test
    void getByIdTest() throws Exception {

        mockMvc.perform(get("/users/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.name").value("Melody Altenwerth DDS"))
                .andExpect(jsonPath("$.email").value("Hermina_Osinski43@hotmail.com"));
    }

    @Test
    void deleteTest() throws Exception {

        UserDto userDto = userService.getById(102);
        assertThat(userDto).isNotNull();

        mockMvc.perform(delete("/users/102"))
                .andExpect(status().isOk());

        assertThrows(
                ElementNotFoundException.class,
                () -> userService.getById(102)
        );
    }
}
