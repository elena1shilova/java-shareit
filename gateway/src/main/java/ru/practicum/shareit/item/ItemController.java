package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.marker.OnCreate;

import java.util.Map;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@NotNull @Validated(OnCreate.class) @RequestBody ItemRequestDto itemDto,
                                         @NotNull @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemClient.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@NotNull @PathVariable Integer itemId, @RequestBody ItemRequestDto itemDto,
                                         @NotNull @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemClient.update(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@NotNull @PathVariable Integer itemId) {
        return itemClient.getById(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@NotNull @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemClient.getAll(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchText(@NotNull @RequestParam String text) {
        return itemClient.searchText(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@NotNull @RequestBody Map<String, String> requestBody,
                                                @NotNull @PathVariable Integer itemId,
                                                @NotNull @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemClient.createComment(requestBody, itemId, userId);
    }
}
