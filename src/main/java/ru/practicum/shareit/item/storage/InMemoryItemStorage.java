package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.HashMap;
import java.util.Map;

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
    public ItemDto create(Item item, Integer userId) {

        if (userId == null) {
            throw new RuntimeException("Id создателя должен быть указан");
        }

        if (item == null) {
            throw new RuntimeException("Добавьте инф-ию о вещи");
        }

        validItem(item);

        User user = UserMapper.toUser(userStorage.getById(userId));
        //Item item = ItemMapper.toItem(itemDto);
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
