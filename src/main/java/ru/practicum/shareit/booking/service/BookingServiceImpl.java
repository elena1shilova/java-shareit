package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ElementNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.text.MessageFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto create(BookingDto bookingDto, Integer userId) {

        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ElementNotFoundException(MessageFormat.format("пользователь с id = {0} не найден", userId));
        }
        bookingDto.setBooker(user.get());

        Booking booking = BookingMapper.toBooking(bookingDto);

        Optional<Item> item = itemRepository.findById(bookingDto.getItemId());
        if (item.isEmpty()) {
            throw new ElementNotFoundException(MessageFormat.format("вещь с id = {0} не найден", bookingDto.getItemId()));
        }
        if (!item.get().getAvailable()) {
            throw new RuntimeException(MessageFormat.format("вещь по id {0} не доступна к бронированию", bookingDto.getItemId()));
        }
        booking.setItem(item.get());
        booking.setStatus(Status.WAITING);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto bookingsApproved(Integer bookingId, Boolean approved, Integer userId) {

        Booking booking = findById(bookingId);

        if (booking.getItem().getOwner().getId().equals(userId)) {
            booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
            return BookingMapper.toBookingDto(booking);
        }

        throw new ValidationException(MessageFormat.format("пользователь id = {0} не является владельцем вещи", userId));
    }

    private Booking findById(Integer id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        if (booking.isPresent()) {
            return booking.get();
        }
        throw new ElementNotFoundException(MessageFormat.format("бронирование с id = {0} не найдено", id));
    }
}
