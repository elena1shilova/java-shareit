package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserStorage {

    UserDto create(UserDto userDto);

    UserDto update(UserDto newDtoUser);

    UserDto getById(Integer id);

    void delete(Integer id);
}
