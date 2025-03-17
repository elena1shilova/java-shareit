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
import ru.practicum.shareit.user.model.User;
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

    private static final User USER_NEW = User.builder()
            .name("QWER")
            .email("QWER@hotmail.com")
            .build();

    private static final User USER_UPDATE = User.builder()
            .name("QAZWSX")
            .email("QAZWSX@hotmail.com")
            .build();

    private static final User USER_NEW_EX = User.builder()
            .name("QWER")
            .email("Hermina_Osinski43@hotmail.com")
            .build();

    @Test
    void createTest() throws Exception {

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(USER_NEW)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("QWER"))
                .andExpect(jsonPath("$.email").value("QWER@hotmail.com"));
    }

    @Test
    void createExceptionTest() throws Exception {

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(USER_NEW_EX)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateTest() throws Exception {

        mockMvc.perform(patch("/users/101")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(USER_UPDATE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(101))
                .andExpect(jsonPath("$.name").value("QAZWSX"))
                .andExpect(jsonPath("$.email").value("QAZWSX@hotmail.com"));

        mockMvc.perform(patch("/users/105")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(USER_UPDATE)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateExceptionTest() throws Exception {

        mockMvc.perform(patch("/users/101")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(USER_NEW_EX)))
                .andExpect(status().isInternalServerError());
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

        mockMvc.perform(delete("/users/105"))
                .andExpect(status().isNotFound());
    }
}
