package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByBooker_Id(Integer id, Sort sort);

    Optional<Booking> findByItem_Id(Integer id);

    List<Booking> findByItem_Owner_Id(Integer userId, Sort sort);

    List<Booking> findByItem_Owner_IdAndEndIsBefore(Integer userId, LocalDateTime now, Sort sort);

    List<Booking> findByItem_Owner_IdAndStartIsAfter(Integer userId, LocalDateTime now, Sort sort);

    List<Booking> findByItem_Owner_IdAndStatus(Integer userId, Status status, Sort sort);

    List<Booking> findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(Integer userId, LocalDateTime now1, LocalDateTime now2, Sort sort);

    List<Booking> findByBooker_IdAndEndIsBefore(Integer userId, LocalDateTime now, Sort sort);

    List<Booking> findByBooker_IdAndStartIsAfter(Integer userId, LocalDateTime now, Sort sort);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(Integer userId, LocalDateTime now, LocalDateTime now1, Sort sort);

    List<Booking> findByBooker_IdAndStatus(Integer userId, Status rejected, Sort sort);

    List<Booking> findByItem_IdAndStatus(Integer id, Status approved, Sort sort);

    List<Booking> findByItemInAndStatus(List<Item> itemList, Status approved, Sort sort);
}
