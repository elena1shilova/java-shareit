package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item create(Item item);

    Item getById(Integer itemId);

    ItemDto update(Item item, ItemDto itemDto);

    List<Item> getAll(Integer userId);

    List<Item> searchText(String text);
}
