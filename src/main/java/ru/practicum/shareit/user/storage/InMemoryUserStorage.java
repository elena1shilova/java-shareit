package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private Integer userId = 1;

    @Override
    public User create(User user) {
        user.setId(userId);
        userId++;
        users.put(user.getId(), user);
        log.debug("Пользователь успешно создан");
        return user;
    }

    @Override
    public User update(User newUser) {

        log.debug("Пользователь успешно обновлен");
        return users.put(newUser.getId(), newUser);
    }

    @Override
    public User getById(Integer id) {
        return users.get(id);
    }

    @Override
    public void delete(Integer id) {
        users.remove(id);
    }

    @Override
    public Optional<User> getUserForEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }
}
