package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestReqDto;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {

    @Autowired
    private static final String API_PREFIX = "/requests";

    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(ItemRequestReqDto itemDto, Integer userId) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> getAllByUserId(Integer userId) {
        return get("", userId, null);
    }

    public ResponseEntity<Object> getAllByNotUserId(Integer userId) {
        return get("/all", userId, null);
    }

    public ResponseEntity<Object> getByRequestId(Integer requestId) {
        Map<String, Object> parameters = Map.of(
                "requestId", requestId
        );
        return get("/{requestId}", null, parameters);
    }
}
