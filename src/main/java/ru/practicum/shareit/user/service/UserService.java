package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto create(@RequestBody UserDto userDto) {
        return userStorage.create(userDto);
    }

    public UserDto update(UserDto newDtoUser) {
        return userStorage.update(newDtoUser);
    }

    public UserDto getById(Integer id) {
        return userStorage.getById(id);
    }

    public void delete(Integer id) {
        userStorage.delete(id);
    }
}
