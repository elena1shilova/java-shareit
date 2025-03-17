package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ElementNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.text.MessageFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {

        User user = UserMapper.toUser(userDto);
        if (userRepository.findByEmailIgnoreCase(user.getEmail()).isPresent()) {
            throw new RuntimeException("Такой email уже зарегистрирован");
        }

        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto update(Integer id, UserDto newDtoUser) {

        User oldUser = userRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException(MessageFormat.format("Пользователь с ид {0} не найден", id)));

        User newUser = UserMapper.toUser(newDtoUser);
        Optional<User> user = userRepository.findByEmailIgnoreCase(newDtoUser.getEmail());
        if (user.isPresent() && !id.equals(user.get().getId())) {
            throw new RuntimeException("Такой email уже зарегистрирован");
        }

        newUser.setId(id);
        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        return UserMapper.toUserDto(userRepository.save(oldUser));
    }

    @Override
    public UserDto getById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException(MessageFormat.format("id = {0} не найден", id)));
        return UserMapper.toUserDto(user);

    }

    @Override
    public void delete(Integer id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException(MessageFormat.format("id = {0} не найден", id)));
        userRepository.delete(user);
    }
}
