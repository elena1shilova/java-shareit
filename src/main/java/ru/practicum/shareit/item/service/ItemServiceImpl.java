package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ElementNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.DateDto;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    private static final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    @Override
    public ItemDto create(ItemDto itemDto, Integer userId) {

        Item item = ItemMapper.toItem(itemDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ElementNotFoundException(MessageFormat.format("Пользователь с id {0} не найдена", userId)));

        item.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getById(Integer itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ElementNotFoundException(MessageFormat.format("Вещь с id {0} не найдена", itemId)));

        LocalDateTime now = LocalDateTime.now();
        ItemDto dto = ItemMapper.toItemDto(item);
        List<Booking> bookingList = bookingRepository.findByItem_IdAndStatus(item.getId(), Status.APPROVED, sort);
        addDateLastAndNext(dto, bookingList, now);

        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        dto.setComments(comments);
        return dto;
    }

    @Override
    public ItemDto update(Integer itemId, ItemDto itemDto, Integer userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ElementNotFoundException(MessageFormat.format("Вещь с id {0} не найдена", itemId)));

        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new ElementNotFoundException("Идентификаторы создателя и пользователя, изменяющего инф-ию о вещи, не совпадают");
        }

        item.setName(itemDto.getName() != null ? itemDto.getName() : item.getName());
        item.setDescription(itemDto.getDescription() != null ? itemDto.getDescription() : item.getDescription());
        item.setAvailable(itemDto.getAvailable() != null ? itemDto.getAvailable() : item.getAvailable());

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public List<ItemDto> getAll(Integer userId) {
        List<Item> itemList = itemRepository.findByOwnerId(userId);
        List<ItemDto> itemDtos = new ArrayList<>();
        List<Booking> bookingList = bookingRepository.findByItemInAndStatus(itemList, Status.APPROVED, sort);
        Map<Item, List<Booking>> groupedBookings = new HashMap<>();
        Map<Item, List<Comment>> groupedComments = new HashMap<>();

        if (bookingList != null && !bookingList.isEmpty()) {
            groupedBookings = bookingList.stream()
                    .collect(Collectors.groupingBy(
                                    Booking::getItem,
                                    LinkedHashMap::new,
                                    Collectors.toCollection(ArrayList::new)
                            )
                    );
        }
        List<Comment> commentsList = commentRepository.findAllByItemIn(itemList);
        if (commentsList != null && !commentsList.isEmpty()) {
            groupedComments = commentsList.stream()
                    .collect(Collectors.groupingBy(Comment::getItem));
        }
        for (Item item : itemList) {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            if (groupedBookings != null && !groupedBookings.isEmpty()) {
                addDateLastAndNext(itemDto, groupedBookings.get(item), LocalDateTime.now());
            }
            if (groupedComments != null && !groupedComments.isEmpty()) {
                itemDto.setComments(groupedComments.get(item));
            }
            itemDtos.add(itemDto);
        }
        return itemDtos;
    }

    private void addDateLastAndNext(ItemDto dto, List<Booking> bookingList, LocalDateTime now) {
        if (bookingList.size() > 1) {
            if (bookingList.get(0).getStart().isBefore(now)) {
                dto.setLastBooking(
                        new DateDto(
                                bookingList.get(0).getStart(),
                                bookingList.get(0).getEnd()
                        ));
                dto.setNextBooking(
                        new DateDto(
                                bookingList.get(1).getStart(),
                                bookingList.get(1).getEnd()
                        ));
            } else {
                dto.setNextBooking(
                        new DateDto(
                                bookingList.get(0).getStart(),
                                bookingList.get(0).getEnd()
                        )
                );
            }
        } else if (bookingList.size() == 1) {
            if (bookingList.get(0).getStart().isBefore(now)) {
                dto.setLastBooking(
                        new DateDto(
                                bookingList.get(0).getStart(),
                                bookingList.get(0).getEnd()
                        )
                );
            } else {
                dto.setNextBooking(
                        new DateDto(
                                bookingList.get(0).getStart(),
                                bookingList.get(0).getEnd()
                        )
                );
            }
        }
    }

    @Override
    public List<ItemDto> searchText(String text) {
        if (!text.isEmpty()) {
            return itemRepository.searchItemsByText(text).stream()
                    .filter(Item::getAvailable)
                    .map(ItemMapper::toItemDto)
                    .toList();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public CommentDto createComment(String text, Integer itemId, Integer userId) {

        Optional<Booking> booking = bookingRepository.findByItem_Id(itemId);

        if (booking.isEmpty()) {
            throw new ElementNotFoundException(MessageFormat.format("Бронь для вещи с id {0} не найдена", itemId));
        }

        LocalDateTime now = LocalDateTime.now();
        if (booking.get().getEnd().isAfter(now)) {
            throw new RuntimeException("У вещи еще не закончился срок аренды!");
        }
        if (!booking.get().getBooker().getId().equals(userId)) {
            throw new ElementNotFoundException(MessageFormat.format("Пользователь с id {0} не бронировал вещь с id {1}", userId, itemId));
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ElementNotFoundException(MessageFormat.format("Вещь с id = {0} не найдена", itemId)));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ElementNotFoundException(MessageFormat.format("Пользователь с id = {0} не найден", userId)));

        return CommentMapper.toCommentDto(
                commentRepository.save(
                        new Comment(text, item, user, now)));
    }
}
