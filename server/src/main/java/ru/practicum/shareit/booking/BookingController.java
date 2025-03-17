package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestBody BookingDto bookingDto,
                             @RequestHeader("X-Sharer-User-Id") Integer userId) {

        return bookingService.create(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto bookingsApproved(@PathVariable Integer bookingId,
                                       @RequestParam Boolean approved,
                                       @RequestHeader("X-Sharer-User-Id") Integer userId) {

        return bookingService.bookingsApproved(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findByIdBooking(@PathVariable Integer bookingId,
                                      @RequestHeader("X-Sharer-User-Id") Integer userId) {

        return bookingService.findByIdBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findAllForState(@RequestParam State state,
                                            @RequestHeader("X-Sharer-User-Id") Integer userId) {

        return bookingService.findAllForState(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllOwnerForState(@RequestParam State state,
                                                 @RequestHeader("X-Sharer-User-Id") Integer userId) {

        return bookingService.findAllOwnerForState(state, userId);
    }
}
