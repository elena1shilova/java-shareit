package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Integer, Item> items = new HashMap<>();
    private Integer itemId = 1;

    @Override
    public Item create(Item item, Integer userId, User user) {
        item.setOwner(user);
        item.setId(itemId);
        itemId++;
        items.put(item.getId(), item);
        log.debug("Вещь успешно создана");
        return item;
    }

    @Override
    public Item getById(Integer itemId) {
        return items.get(itemId);
    }

    @Override
    public ItemDto update(Item item, ItemDto itemDto) {
        item.setName(itemDto.getName() != null ? itemDto.getName() : item.getName());
        item.setDescription(itemDto.getDescription() != null ? itemDto.getDescription() : item.getDescription());
        item.setAvailable(itemDto.getAvailable() != null ? itemDto.getAvailable() : item.getAvailable());
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<Item> getAll(Integer userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .toList();
    }

    @Override
    public List<Item> searchText(String text) {
        return items.values().stream()
                .filter(item -> (item.getName().toUpperCase().contains(text.toUpperCase())
                        || item.getDescription().toUpperCase().contains(text.toUpperCase()))
                        && item.getAvailable())
                .toList();
    }
}
