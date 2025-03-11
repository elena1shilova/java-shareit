package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ElementNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemDto create(ItemDto itemDto, Integer userId) {
        Item item = ItemMapper.toItem(itemDto);

        User user = userStorage.getById(userId);

        if (user == null) {
            throw new ElementNotFoundException(MessageFormat.format("Пользователь с ид {0} не найден", userId));
        }

        item.setOwner(user);

        return ItemMapper.toItemDto(itemStorage.create(item));
    }

    public ItemDto getById(Integer itemId) {
        return ItemMapper.toItemDto(itemStorage.getById(itemId));
    }

    public ItemDto update(Integer itemId, ItemDto itemDto, Integer userId) {
        Item item = itemStorage.getById(itemId);
        if (item == null) {
            throw new RuntimeException("По заданному ид вещь не найдена");
        }
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new ElementNotFoundException("Идентификаторы создателя и пользователя, изменяющего инф-ию о вещи, не совпадают");
        }
        return itemStorage.update(item, itemDto);
    }

    public List<ItemDto> getAll(Integer userId) {
        return itemStorage.getAll(userId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    public List<ItemDto> searchText(String text) {
        if (!text.isEmpty()) {
            return itemStorage.searchText(text).stream()
                    .map(ItemMapper::toItemDto)
                    .toList();
        } else {
            return new ArrayList<>();
        }
    }
}
