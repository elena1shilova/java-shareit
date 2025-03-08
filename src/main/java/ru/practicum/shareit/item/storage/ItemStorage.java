package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public interface ItemStorage {
    ItemDto create(Item item, Integer userId);

    ItemDto getById(Integer itemId);
}
