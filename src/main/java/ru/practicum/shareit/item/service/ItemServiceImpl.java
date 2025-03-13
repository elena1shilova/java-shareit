package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ElementNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto create(ItemDto itemDto, Integer userId) {

        Item item = ItemMapper.toItem(itemDto);
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new ElementNotFoundException(MessageFormat.format("Пользователь с ид {0} не найден", userId));
        }

        item.setOwner(user.get());
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getById(Integer itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isPresent()) {

            ItemDto dto = ItemMapper.toItemDto(getItemStartAndEndData(item.get()));
            List<Comment> comments = commentRepository.findAllByItemId(itemId);
            dto.setComments(comments);
            return dto;
        }
        throw new ElementNotFoundException(MessageFormat.format("id = {0} не найден", itemId));
    }

    @Override
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

    @Override
    public List<ItemDto> getAll(Integer userId) {
        return itemRepository.findByOwnerId(userId).stream()
                .map(this::getItemStartAndEndData)
                .map(ItemMapper::toItemDto)
                .toList();
    }

    private Item getItemStartAndEndData(Item item) {
        Booking booking = bookingRepository.findByItem_Id(item.getId());
        if (booking != null) {
            item.setStart(booking.getStart());
            item.setEnd(booking.getEnd());
        }
        return item;
    }

    @Override
    public List<ItemDto> searchText(String text) {
        if (!text.isEmpty()) {
            return itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text).stream()
                    .filter(Item::getAvailable)
                    .map(ItemMapper::toItemDto)
                    .toList();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public CommentDto createComment(String text, Integer itemId, Integer userId) {

        Booking booking = bookingRepository.findByItem_Id(itemId);
        assert booking != null;

        LocalDateTime now = LocalDateTime.now();
        if (booking.getEnd().isAfter(now)) {
            throw new RuntimeException("У вещи еще не закончился срок аренды!");
        }
        if (booking.getBooker().getId().equals(userId)) {
            Optional<Item> item = itemRepository.findById(itemId);
            Optional<User> user = userRepository.findById(userId);

            return CommentMapper.toCommentDto(
                    commentRepository.save(
                            new Comment(text, item.get(), user.get(), now)));
        }
        throw new ElementNotFoundException(MessageFormat.format("Пользователь с id {0} не бронировал вещь с id {1}", userId, itemId));
    }
}
