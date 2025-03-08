package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto create(@RequestBody User user) {
        return userStorage.create(user);
    }

    public UserDto update(User newUser) {
        return userStorage.update(newUser);
    }

    public UserDto getById(Integer id) {
        return userStorage.getById(id);
    }

    public void delete(Integer id) {
        userStorage.delete(id);
    }
}
