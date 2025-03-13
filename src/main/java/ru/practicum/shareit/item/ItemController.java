package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.marker.OnCreate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@NotNull @Validated(OnCreate.class) @RequestBody ItemDto itemDto,
                          @NotNull @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@NotNull @PathVariable Integer itemId, @RequestBody ItemDto itemDto,
                          @NotNull @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.update(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@NotNull @PathVariable Integer itemId) {
        return itemService.getById(itemId);
    }

    @GetMapping
    public List<ItemDto> getAll(@NotNull @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchText(@NotNull @RequestParam String text) {
        return itemService.searchText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@NotNull @RequestBody Map<String, String> requestBody,
                                    @NotNull @PathVariable Integer itemId,
                                    @NotNull @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.createComment(requestBody.get("text"), itemId, userId);
    }
}
