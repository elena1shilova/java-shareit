package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ElementNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.text.MessageFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public UserDto create(UserDto userDto) {

        if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            throw new ValidationException("Email при создании пользователя должен быть указан");
        }

        User user = UserMapper.toUser(userDto);
        if (userStorage.getUserForEmail(userDto.getEmail()).isPresent()) {
            throw new RuntimeException("Такой email уже зарегистрирован");
        }
        return UserMapper.toUserDto(userStorage.create(user));
    }

    public UserDto update(Integer id, UserDto newDtoUser) {
        if (userStorage.getById(id) == null) {
            throw new ElementNotFoundException(MessageFormat.format("Пользователь с ид {0} не найден", id));
        }
        User newUser = UserMapper.toUser(newDtoUser);
        Optional<User> user = userStorage.getUserForEmail(newDtoUser.getEmail());
        if (user.isPresent() && !newUser.getId().equals(user.get().getId())) {
            throw new RuntimeException("Такой email уже зарегистрирован");
        }
        if (userStorage.getById(id) != null) {
            return UserMapper.toUserDto(userStorage.update(id, newUser));
        }
        throw new ElementNotFoundException(MessageFormat.format("id = {0} не найден", newUser.getId()));

    }

    public UserDto getById(Integer id) {
        User user = userStorage.getById(id);
        if (user != null) {
            return UserMapper.toUserDto(userStorage.getById(id));
        }
        throw new ElementNotFoundException(MessageFormat.format("id = {0} не найден", id));


    }

    public void delete(Integer id) {

        User user = userStorage.getById(id);
        if (user != null) {
            userStorage.delete(id);
        } else {
            throw new ElementNotFoundException(MessageFormat.format("id = {0} не найден", id));
        }
    }
}
