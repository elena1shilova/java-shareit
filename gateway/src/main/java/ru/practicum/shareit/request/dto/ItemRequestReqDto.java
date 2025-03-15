package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.practicum.shareit.marker.OnCreate;

import java.time.LocalDateTime;

@Data
public class ItemRequestReqDto {

    @NotBlank(groups = OnCreate.class)
    private String description;
    private Integer requestorId;
    private LocalDateTime created;
}
