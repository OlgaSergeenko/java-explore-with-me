package ru.practicum.publicUser;

import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.baseClient.BaseClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PublicUserClient extends BaseClient {

    private static final String API_PREFIX = "";

    @Autowired
    public PublicUserClient(@Value("${exploreWithMe-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> findAllEvents(String text,
                                                List<Integer> categories,
                                                Boolean paid,
                                                String rangeStart,
                                                String rangeEnd,
                                                Boolean onlyAvailable,
                                                String sort,
                                                Integer from,
                                                Integer size) {
        Map<String, Object> parameters = new HashMap<>();
        StringBuilder sb = new StringBuilder();

        if (Optional.ofNullable(text).isPresent()) {
            parameters.put("text", text);
            sb.append("&text={text}");
        }
        if (Optional.ofNullable(categories).isPresent()) {
            parameters.put("categories", StringUtils.join(categories.stream().map(String::valueOf).collect(Collectors.toList()), ','));
            sb.append("&categories={categories}");
        }
        if (Optional.ofNullable(paid).isPresent()) {
            parameters.put("paid", paid);
            sb.append("&paid={paid}");
        }

        if (Optional.ofNullable(rangeStart).isPresent()) {
            parameters.put("rangeStart", rangeStart);
            sb.append("&rangeStart={rangeStart}");
        }

        if (Optional.ofNullable(rangeEnd).isPresent()) {
            parameters.put("rangeEnd", rangeEnd);
            sb.append("&rangeEnd={rangeEnd}");
        }

        parameters.put("onlyAvailable", onlyAvailable);

        if (Optional.ofNullable(sort).isPresent()) {
            parameters.put("sort", sort);
            sb.append("&sort={sort}");
        }
        parameters.put("from", from);
        parameters.put("size", size);

        return get("/events?" +
                "onlyAvailable={onlyAvailable}" +
                "&from={from}" +
                "&size={size}" + sb, null, parameters);
    }

    public ResponseEntity<Object> getEventInfo(long eventId) {
        return get("/events/" + eventId);
    }

    public ResponseEntity<Object> findAllCategories(Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/categories?from={from}&size={size}", null, parameters);
    }

    public ResponseEntity<Object> findCategory(int catId) {
        return get("/categories/" + catId);
    }

    public ResponseEntity<Object> findAllCompilations(Boolean pinned,
                                                      Integer from,
                                                      Integer size) {
        Map<String, Object> parameters = new HashMap<>();
        StringBuilder sb = new StringBuilder();

        if (Optional.ofNullable(pinned).isPresent()) {
            parameters.put("pinned", pinned);
            sb.append("&pinned={pinned}");
        }
        parameters.put("from", from);
        parameters.put("size", size);

        return get("/compilations?from={from}&size={size}" + sb, null, parameters);
    }

    public ResponseEntity<Object> findCompilation(int compId) {
        return get("/compilations/" + compId);
    }
}
