package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingDto bookingDto, Integer userId);

    BookingDto bookingsApproved(Integer bookingId, Boolean approved, Integer userId);

    BookingDto findByIdBooking(Integer bookingId, Integer userId);

    List<BookingDto> findAllForState(State state, Integer userId);

    List<BookingDto> findAllOwnerForState(State state, Integer userId);
}
