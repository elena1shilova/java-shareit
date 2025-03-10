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
    public User update(Integer id, User newUser) {
        User oldUser = users.get(id);

        newUser.setId(id);

        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        log.debug("Пользователь успешно обновлен");
        return newUser;
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
