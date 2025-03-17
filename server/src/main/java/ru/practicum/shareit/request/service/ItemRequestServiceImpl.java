package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ElementNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Integer userId) {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ElementNotFoundException(MessageFormat.format("Пользователь с id {0} не найдена", userId)));
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());

        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getAllByUserId(Integer userId) {

        List<ItemRequest> itemRequest = itemRequestRepository.findByRequestor_Id(userId);
        if (itemRequest == null || itemRequest.isEmpty()) {
            throw new ElementNotFoundException(MessageFormat.format("Запросы по пользователю с id {0} не найдены", userId));
        }
        return convertItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getAllByNotUserId(Integer userId) {
        List<ItemRequest> itemRequest = itemRequestRepository.findByRequestor_IdNot(userId);
        if (itemRequest == null || itemRequest.isEmpty()) {
            throw new ElementNotFoundException("Запросы не найдены");
        }
        return convertItemRequestDto(itemRequest);
    }

    @Override
    public ItemRequestDto getByRequestId(Integer requestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ElementNotFoundException(MessageFormat.format("Запрос с id {0} не найден", requestId)));

        return convertItemRequestDto(List.of(itemRequest)).getFirst();
    }

    private List<ItemRequestDto> convertItemRequestDto(List<ItemRequest> itemRequest) {

        List<Integer> list = itemRequest.stream().map(ItemRequest::getId).toList();
        List<Item> h = itemRepository.findAllByRequest_IdIn(list);
        Map<ItemRequest, List<Item>> groupedItems = itemRepository.findAllByRequest_IdIn(list).stream()
                .collect(Collectors.groupingBy(Item::getRequest));

        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        for (ItemRequest item : itemRequest) {
            ItemRequestDto itemDto = ItemRequestMapper.toItemRequestDto(item);
            if (!groupedItems.isEmpty()) {
                itemDto.setItems(groupedItems.get(item));
            }
            itemRequestDtos.add(itemDto);
        }

        return itemRequestDtos;
    }
}
