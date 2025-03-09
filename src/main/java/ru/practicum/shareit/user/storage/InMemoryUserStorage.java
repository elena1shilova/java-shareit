package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ElementNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private final Set<String> emailSet = new HashSet<>();
    private Integer userId = 1;

    @Override
    public UserDto create(UserDto userDto) {

        if (userDto.getEmail() == null) {
            throw new ValidationException("Имейл должен быть указан");
        }

        User user = UserMapper.toUser(userDto);
        validUser(user);
        user.setId(userId);
        userId++;
        users.put(user.getId(), user);
        emailSet.add(user.getEmail());
        log.debug("Пользователь успешно создан");
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(UserDto newDtoUser) {
        if (newDtoUser.getId() == null) {
            throw new RuntimeException("Id должен быть указан");
        }

        User newUser = UserMapper.toUser(newDtoUser);

        if (users.containsKey(newUser.getId())) {

            validUser(newUser);
            User oldUser = users.get(newUser.getId());

            if (!oldUser.getEmail().equals(newUser.getEmail()) && emailSet.contains(newUser.getEmail())) {
                throw new ValidationException("Этот имейл уже используется");
            }
            if (newUser.getEmail() != null) {
                oldUser.setEmail(newUser.getEmail());
            }
            if (newUser.getName() != null) {
                oldUser.setName(newUser.getName());
            }
            log.debug("Пользователь успешно обновлен");
            return UserMapper.toUserDto(newUser);
        }
        throw new ElementNotFoundException(MessageFormat.format("id = {0} не найден", newUser.getId()));
    }

    @Override
    public UserDto getById(Integer id) {
        if (id == null) {
            throw new RuntimeException("Id должен быть указан");
        }
        if (users.containsKey(id)) {
            return UserMapper.toUserDto(users.get(id));
        }
        throw new ElementNotFoundException(MessageFormat.format("id = {0} не найден", id));
    }

    @Override
    public void delete(Integer id) {
        if (id == null) {
            throw new RuntimeException("Id должен быть указан");
        }
        if (users.containsKey(id)) {
            users.remove(id);
        } else {
            throw new ElementNotFoundException(MessageFormat.format("id = {0} не найден", id));
        }
    }

    private void validUser(User user) {

        if (emailSet.contains(user.getEmail())) {
            throw new RuntimeException("Такой email уже зарегистрирован");
        }
        if (user.getEmail() != null && !user.getEmail().contains("@")) {
            throw new ValidationException("Имейл должен содержать @");
        }
    }
}
