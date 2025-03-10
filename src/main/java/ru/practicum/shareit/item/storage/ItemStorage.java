package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemStorage {
    Item create(Item item, Integer userId, User userd);

    Item getById(Integer itemId);

    ItemDto update(Item item, ItemDto itemDto);

    List<Item> getAll(Integer userId);

    List<Item> searchText(String text);
}
