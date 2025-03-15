package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestDto itemDto, Integer userId);

    List<ItemRequestDto> getAllByUserId(Integer userId);

    List<ItemRequestDto> getAllByNotUserId(Integer userId);

    ItemRequestDto getByRequestId(Integer requestId);
}
