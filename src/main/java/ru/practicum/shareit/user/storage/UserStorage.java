package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public interface UserStorage {

    UserDto create(User user);

    UserDto update(User newUser);

    UserDto getById(Integer id);

    void delete(Integer id);
}
