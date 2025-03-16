package ru.practicum.shareit.item;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.DateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.marker.OnCreate;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoJsonTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    public void testDeserialize() throws Exception {
        String content = "{\"id\":1,\"name\":\"Test Item\",\"description\":\"Test Description\",\"available\":true,\"ownerId\":1}";
        ItemDto itemDto = new ItemDto(1, "Test Item", "Test Description", true, new User(),
                123, new DateDto(), new DateDto(), List.of(new Comment()));

        assertThat(json.parseObject(content).getId()).isEqualTo(1);
        assertThat(json.parseObject(content).getName()).isEqualTo("Test Item");
    }

    @Test
    public void testValidation() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        ItemDto itemDto = new ItemDto();
        itemDto.setName("");
        itemDto.setDescription("");
        itemDto.setAvailable(null);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto, OnCreate.class);

        assertThat(violations).hasSize(3);
        assertThat(violations).extracting("message").contains(
                "must not be blank",
                "must not be null",
                "must not be blank"
        );
    }
}
