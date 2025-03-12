package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;
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
import java.util.List;
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

    @Override
    public BookingDto findByIdBooking(Integer bookingId, Integer userId) {
        Booking booking = findById(bookingId);

        if (booking.getItem().getOwner().getId().equals(userId) || booking.getBooker().getId().equals(userId)) {
            return BookingMapper.toBookingDto(booking);
        }

        throw new ValidationException(MessageFormat.format("пользователь id = {0} не является ни владельцем вещи, ни автором бронирования", userId));
    }

    @Override
    public List<BookingDto> findAllForState(State state, Integer userId) {

        return getBookingList(state, userId).stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    @Override
    public List<BookingDto> findAllOwnerForState(State state, Integer userId) {

        List<Booking> bookings = getBookingList(state, userId);
        if (bookings != null && !bookings.isEmpty()) {
            return bookings.stream()
                    .map(BookingMapper::toBookingDto)
                    .toList();
        }
        throw new RuntimeException(MessageFormat.format("Пользователь с ид {0} не является владельцем даже одной вещи", userId));
    }

    private List<Booking> getBookingList(State state, Integer userId) {
        if (state.equals(State.ALL)) {
            return bookingRepository.findAllByBooker_IdOrderByStart(userId);
        } else {
            return bookingRepository.findAllByStatusEqualsAndBooker_IdOrderByStart(getStatusByState(state), userId);
        }
    }

    private Status getStatusByState(State state) {
        switch (state) {
            case CURRENT:
            case FUTURE:
            case REJECTED:
                return Status.REJECTED;
            case WAITING:
                return Status.WAITING;
            case PAST:
                return Status.APPROVED;
            default:
                throw new IllegalArgumentException(MessageFormat.format("Некорректное значение state: ", state));
        }
    }

    private Booking findById(Integer id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        if (booking.isPresent()) {
            return booking.get();
        }
        throw new ElementNotFoundException(MessageFormat.format("Бронирование с id = {0} не найдено", id));
    }
}
