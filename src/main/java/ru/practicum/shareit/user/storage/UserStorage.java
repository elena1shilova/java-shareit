package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserStorage {

    User create(User userDto);

    User update(Integer id, User newUser);

    User getById(Integer id);

    void delete(Integer id);

    Optional<User> getUserForEmail(String email);
}
