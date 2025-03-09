package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ElementNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class InMemoryItemStorage implements ItemStorage {

    private final UserStorage userStorage;

    private final Map<Integer, Item> items = new HashMap<>();
    private Integer itemId = 1;

    public InMemoryItemStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public ItemDto create(ItemDto itemDto, Integer userId) {

        if (userId == null) {
            throw new RuntimeException("Id создателя должен быть указан");
        }

        if (itemDto == null) {
            throw new RuntimeException("Добавьте инф-ию о вещи");
        }

        Item item = ItemMapper.toItem(itemDto);

        validItem(item);

        User user = UserMapper.toUser(userStorage.getById(userId));

        item.setOwner(user);
        item.setId(itemId);
        itemId++;
        items.put(item.getId(), item);
        log.debug("Вещь успешно создана");
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getById(Integer itemId) {
        return ItemMapper.toItemDto(items.get(itemId));
    }

    @Override
    public ItemDto update(Integer itemId, ItemDto itemDto, Integer userId) {
        if (userId == null) {
            throw new RuntimeException("Id создателя должен быть указан");
        }
        if (itemId == null) {
            throw new RuntimeException("Id вещи должен быть указан");
        }
        Item item = items.get(itemId);
        if (item == null) {
            throw new RuntimeException("По заданному ид вещь не найдена");
        }
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new ElementNotFoundException("Идентификаторы создателя и пользователя, изменяющего инф-ию о вещи, не совпадают");
        }

        item.setName(itemDto.getName() != null ? itemDto.getName() : item.getName());
        item.setDescription(itemDto.getDescription() != null ? itemDto.getDescription() : item.getDescription());
        item.setAvailable(itemDto.getAvailable() != null ? itemDto.getAvailable() : item.getAvailable());
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAll(Integer userId) {
        List<ItemDto> list = new ArrayList<>();
        for (Map.Entry<Integer, Item> itemEntry : items.entrySet()) {
            if (itemEntry.getValue().getOwner().getId().equals(userId)) {
                list.add(ItemMapper.toItemDto(itemEntry.getValue()));
            }
        }
        return list;
    }

    @Override
    public List<ItemDto> searchText(String text) {
        List<ItemDto> list = new ArrayList<>();
        for (Map.Entry<Integer, Item> itemEntry : items.entrySet()) {
            if (!text.isEmpty() && (itemEntry.getValue().getName().toUpperCase().contains(text.toUpperCase())
                    || itemEntry.getValue().getDescription().toUpperCase().contains(text.toUpperCase()))
                    && itemEntry.getValue().getAvailable()) {
                list.add(ItemMapper.toItemDto(itemEntry.getValue()));
            }
        }
        return list;
    }

    private void validItem(Item item) {

        if (item.getAvailable() == null) {
            throw new ValidationException("Отсутвует инф-ия о доступности вещи");
        }
        if (item.getName() == null || item.getName().isEmpty()) {
            throw new ValidationException("Отсутвует инф-ия о наименовании вещи");
        }
        if (item.getDescription() == null) {
            throw new ValidationException("Отсутвует инф-ия об описании вещи");
        }
    }
}
