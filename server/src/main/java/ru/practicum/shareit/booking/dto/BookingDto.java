package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.marker.OnCreate;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

    private Integer id;
    @NotNull(groups = OnCreate.class)
    private LocalDateTime start;
    private LocalDateTime end;
    @NotNull(groups = OnCreate.class)
    private Integer itemId;
    private Item item;
    private User booker;
    private Status status;
}
