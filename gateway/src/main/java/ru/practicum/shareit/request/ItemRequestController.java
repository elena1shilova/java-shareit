package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.marker.OnCreate;
import ru.practicum.shareit.request.dto.ItemRequestReqDto;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@NotNull @Validated(OnCreate.class) @RequestBody ItemRequestReqDto itemDto,
                                         @NotNull @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestClient.create(itemDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUserId(@NotNull @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestClient.getAllByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllByNotUserId(@NotNull @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestClient.getAllByNotUserId(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getByRequestId(@NotNull @PathVariable Integer requestId) {
        return itemRequestClient.getByRequestId(requestId);
    }
}
