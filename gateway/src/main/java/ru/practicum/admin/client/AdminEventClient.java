package ru.practicum.admin.client;


import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.admin.dto.AdminUpdateEventRequestDto;
import ru.practicum.admin.dto.EventState;
import ru.practicum.baseClient.BaseClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminEventClient extends BaseClient {

    private static final String API_PREFIX = "/admin/events";

    @Autowired
    public AdminEventClient(@Value("${exploreWithMe-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getEvents(List<Long> users,
                                            List<EventState> states,
                                            List<Integer> categories,
                                            String rangeStart,
                                            String rangeEnd,
                                            Integer from,
                                            Integer size) {
        Map<String, Object> parameters = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        if (Optional.ofNullable(users).isPresent()) {
            parameters.put("users", StringUtils.join(users.stream().map(String::valueOf).collect(Collectors.toList()), ','));
            sb.append("users={users}");
        }
        if (Optional.ofNullable(states).isPresent()) {
            parameters.put("states", StringUtils.join(states.stream().map(String::valueOf).collect(Collectors.toList()), ','));
            sb.append("&states={states}");
        }
        if (Optional.ofNullable(categories).isPresent()) {
            parameters.put("categories", StringUtils.join(categories.stream().map(String::valueOf).collect(Collectors.toList()), ','));
            sb.append("&categories={categories}");
        }
        if (Optional.ofNullable(rangeStart).isPresent()) {
            parameters.put("rangeStart", rangeStart);
            sb.append("&rangeStart={rangeStart}");
        }
        if (Optional.ofNullable(rangeEnd).isPresent()) {
            parameters.put("rangeEnd", rangeEnd);
            sb.append("&rangeEnd={rangeEnd}");
        }
        parameters.put("from", from);
        parameters.put("size", size);

        return get("?" + sb +
                "&from={from}" +
                "&size={size}", null, parameters);
    }

    public ResponseEntity<Object> updateEvent(long eventId, AdminUpdateEventRequestDto eventRequestDto) {
        return put("/" + eventId, eventId, eventRequestDto);
    }

    public ResponseEntity<Object> publishEvent(long eventId) {
        return patch("/" + eventId + "/publish", eventId);
    }

    public ResponseEntity<Object> rejectEvent(long eventId) {
        return patch("/" + eventId + "/reject", eventId);
    }
}

