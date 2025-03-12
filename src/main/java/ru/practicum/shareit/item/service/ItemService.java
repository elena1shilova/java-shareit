package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto itemDto, Integer userId);

    ItemDto getById(Integer itemId);

    ItemDto update(Integer itemId, ItemDto itemDto, Integer userId);

    List<ItemDto> getAll(Integer userId);

    List<ItemDto> searchText(String text);
}
