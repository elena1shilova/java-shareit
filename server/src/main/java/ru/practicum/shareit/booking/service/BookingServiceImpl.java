package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
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
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto create(BookingDto bookingDto, Integer userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ElementNotFoundException(MessageFormat.format("Пользователь с id {0} не найдена", userId)));

        bookingDto.setBooker(user);
        Booking booking = BookingMapper.toBooking(bookingDto);

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ElementNotFoundException(MessageFormat.format("Вещь с id {0} не найдена", bookingDto.getItemId())));

        if (!item.getAvailable()) {
            throw new RuntimeException(MessageFormat.format("Вещь по id {0} не доступна к бронированию", bookingDto.getItemId()));
        }
        booking.setItem(item);
        booking.setStatus(Status.WAITING);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto bookingsApproved(Integer bookingId, Boolean approved, Integer userId) {

        Booking booking = findById(bookingId);

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException(MessageFormat.format("Пользователь id = {0} не является владельцем вещи", userId));
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto findByIdBooking(Integer bookingId, Integer userId) {

        Booking booking = findById(bookingId);

        if (!booking.getItem().getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new ValidationException(MessageFormat.format("Пользователь id = {0} не является ни владельцем вещи, ни автором бронирования", userId));
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> findAllForState(State state, Integer userId) {

        return getBookingList(state, userId).stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    @Override
    public List<BookingDto> findAllOwnerForState(State state, Integer userId) {

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL: {
                bookings = bookingRepository.findByItem_Owner_Id(userId, sort);
                break;
            }
            case PAST: {
                bookings = bookingRepository.findByItem_Owner_IdAndEndIsBefore(userId, now, sort);
                break;
            }
            case FUTURE: {
                bookings = bookingRepository.findByItem_Owner_IdAndStartIsAfter(userId, now, sort);
                break;
            }
            case CURRENT: {
                bookings = bookingRepository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(userId, now, now, sort);
                break;
            }
            case REJECTED: {
                bookings = bookingRepository.findByItem_Owner_IdAndStatus(userId, Status.REJECTED, sort);
                break;
            }
            case WAITING: {
                bookings = bookingRepository.findByItem_Owner_IdAndStatus(userId, Status.WAITING, sort);
                break;
            }
            default:
                throw new IllegalArgumentException(MessageFormat.format("Некорректное значение state: ", state));
        }
        if (bookings == null || bookings.isEmpty()) {
            throw new RuntimeException(MessageFormat.format("Пользователь с ид {0} не является владельцем даже одной вещи", userId));
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    private List<Booking> getBookingList(State state, Integer userId) {

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL: {
                bookings = bookingRepository.findAllByBooker_Id(userId, sort);
                break;
            }
            case PAST: {
                bookings = bookingRepository.findByBooker_IdAndEndIsBefore(userId, now, sort);
                break;
            }
            case FUTURE: {
                bookings = bookingRepository.findByBooker_IdAndStartIsAfter(userId, now, sort);
                break;
            }
            case CURRENT: {
                bookings = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(userId, now, now, sort);
                break;
            }
            case REJECTED: {
                bookings = bookingRepository.findByBooker_IdAndStatus(userId, Status.REJECTED, sort);
                break;
            }
            case WAITING: {
                bookings = bookingRepository.findByBooker_IdAndStatus(userId, Status.WAITING, sort);
                break;
            }
            default:
                throw new IllegalArgumentException(MessageFormat.format("Некорректное значение state: ", state));
        }
        return bookings;
    }

    private Booking findById(Integer id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException(MessageFormat.format("Бронь с id {0} не найдена", id)));
    }
}
