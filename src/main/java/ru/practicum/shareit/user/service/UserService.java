package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto update(Integer id, UserDto newDtoUser);

    UserDto getById(Integer id);

    void delete(Integer id);
}
