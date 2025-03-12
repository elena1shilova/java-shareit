package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ElementNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    public ItemDto create(ItemDto itemDto, Integer userId) {

        Item item = ItemMapper.toItem(itemDto);
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new ElementNotFoundException(MessageFormat.format("Пользователь с ид {0} не найден", userId));
        }

        item.setOwner(user.get());

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    public ItemDto getById(Integer itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isPresent()) {
            return ItemMapper.toItemDto(item.get());
        }
        throw new ElementNotFoundException(MessageFormat.format("id = {0} не найден", itemId));
    }

    public ItemDto update(Integer itemId, ItemDto itemDto, Integer userId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new RuntimeException("По заданному ид вещь не найдена");
        }
        if (!Objects.equals(item.get().getOwner().getId(), userId)) {
            throw new ElementNotFoundException("Идентификаторы создателя и пользователя, изменяющего инф-ию о вещи, не совпадают");
        }

        item.get().setName(itemDto.getName() != null ? itemDto.getName() : item.get().getName());
        item.get().setDescription(itemDto.getDescription() != null ? itemDto.getDescription() : item.get().getDescription());
        item.get().setAvailable(itemDto.getAvailable() != null ? itemDto.getAvailable() : item.get().getAvailable());

        return ItemMapper.toItemDto(itemRepository.save(item.get()));
    }

    public List<ItemDto> getAll(Integer userId) {
        return itemRepository.findAll().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .map(ItemMapper::toItemDto)
                .toList();
    }

    public List<ItemDto> searchText(String text) {
        if (!text.isEmpty()) {
            return itemRepository.findByNameIgnoreCaseOrDescriptionIgnoreCase(text, text).stream()
                    .filter(Item::getAvailable)
                    .map(ItemMapper::toItemDto)
                    .toList();
        } else {
            return new ArrayList<>();
        }
    }
}
