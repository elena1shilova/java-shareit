package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Integer, Item> items = new HashMap<>();
    private Integer itemId = 1;

    @Override
    public Item create(Item item) {
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
    public Item update(Item item) {

        return items.put(item.getId(), item);
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
