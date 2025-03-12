package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByBooker_IdOrderByStart(Integer id);

    List<Booking> findAllByStatusEqualsAndBooker_IdOrderByStart(Status state, Integer id);

    Booking findByItem_Id(Integer id);
}
