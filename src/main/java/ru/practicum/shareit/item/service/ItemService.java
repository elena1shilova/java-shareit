package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.List;

@Service
public class ItemService {

    private final ItemStorage itemStorage;

    public ItemService(ItemStorage itemStorage) {
        this.itemStorage = itemStorage;
    }

    public ItemDto create(ItemDto itemDto, Integer userId) {
        return itemStorage.create(itemDto, userId);
    }

    public ItemDto getById(Integer itemId) {
        return itemStorage.getById(itemId);
    }

    public ItemDto update(Integer itemId, ItemDto itemDto, Integer userId) {
        return itemStorage.update(itemId, itemDto, userId);
    }

    public List<ItemDto> getAll(Integer userId) {
        return itemStorage.getAll(userId);
    }

    public List<ItemDto> searchText(String text) {
        return itemStorage.searchText(text);
    }
}
