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

//        if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
//            throw new ValidationException("Email при создании пользователя должен быть указан");
//        }

        User user = UserMapper.toUser(userDto);
        if (userRepository.findByEmailIgnoreCase(user.getEmail()).isPresent()) {
            throw new RuntimeException("Такой email уже зарегистрирован");
        }

        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto update(Integer id, UserDto newDtoUser) {

        Optional<User> oldUser = userRepository.findById(id);

        if (oldUser.isEmpty()) {
            throw new ElementNotFoundException(MessageFormat.format("Пользователь с ид {0} не найден", id));
        }
        User newUser = UserMapper.toUser(newDtoUser);
        Optional<User> user = userRepository.findByEmailIgnoreCase(newDtoUser.getEmail());
        if (user.isPresent() && !newUser.getId().equals(user.get().getId())) {
            throw new RuntimeException("Такой email уже зарегистрирован");
        }

        newUser.setId(id);
        if (newUser.getEmail() != null) {
            oldUser.get().setEmail(newUser.getEmail());
        }
        if (newUser.getName() != null) {
            oldUser.get().setName(newUser.getName());
        }
        return UserMapper.toUserDto(userRepository.save(oldUser.get()));
    }

    @Override
    public UserDto getById(Integer id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return UserMapper.toUserDto(user.get());
        }
        throw new ElementNotFoundException(MessageFormat.format("id = {0} не найден", id));
    }

    @Override
    public void delete(Integer id) {

        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.delete(user.get());
        } else {
            throw new ElementNotFoundException(MessageFormat.format("id = {0} не найден", id));
        }
    }
}
