package ru.practicum.shareitGateway.request;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareitGateway.client.BaseClient;
import ru.practicum.shareitGateway.request.dto.IncomingItemRequestDto;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addItemRequest(long userId, IncomingItemRequestDto incomingItemRequestDto) {
        return post("", userId, incomingItemRequestDto);
    }

    public ResponseEntity<Object> getItemRequestById(long userId, Long requestId) {
        return get("/" + requestId, userId);
    }

    public ResponseEntity<Object> getItemRequestsByOther(long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size);
        return get("/all?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getItemRequestsByAuthor(long userId) {
        return get("", userId);
    }
}