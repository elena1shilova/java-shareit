package ru.practicum.shareit.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.marker.OnCreate;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoJsonTest {
    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    public void testDeserialize() throws Exception {
        String content = "{\"id\":1,\"name\":\"Test User\",\"email\":\"test@mail.ru\"}";
        UserDto userDto = new UserDto(1, "Test User", "test@mail.ru");

        assertThat(json.parseObject(content).getId()).isEqualTo(1);
        assertThat(json.parseObject(content).getName()).isEqualTo("Test User");
    }

    @Test
    public void testValidation() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        UserDto userDto = new UserDto();
        userDto.setEmail("");

        Set<ConstraintViolation<UserDto>> violationsEmpty = validator.validate(userDto, OnCreate.class);

        assertThat(violationsEmpty).hasSize(1);
        assertThat(violationsEmpty).extracting("message").contains(
                "must not be blank"
        );
    }
}
