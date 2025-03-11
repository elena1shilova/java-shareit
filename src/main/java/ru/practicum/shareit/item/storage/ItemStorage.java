package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item create(Item item);

    Item getById(Integer itemId);

    Item update(Item item);

    List<Item> getAll(Integer userId);

    List<Item> searchText(String text);
}
