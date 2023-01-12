package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StatClient extends BaseClient {

    private static final String API_PREFIX = "";

    @Autowired
    public StatClient(@Value("${exploreWithMe-statistics.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> saveNewHit(NewHitRequestDto hitRequestDto) {
        return post("/hit", hitRequestDto);
    }

    public ResponseEntity<Object> getStats(String start, String end, List<String> uris, Boolean unique) {
        Map<String, Object> parameters = new HashMap<>();
        StringBuilder sb = new StringBuilder();

        parameters.put("start", start);
        sb.append("start={start}");

        parameters.put("end", end);
        sb.append("end={end}");

        if (Optional.ofNullable(uris).isPresent()) {
            parameters.put("uris", uris);
            sb.append("uris={uris}");
        }
        if (Optional.ofNullable(unique).isPresent()) {
            parameters.put("unique", unique);
            sb.append("&unique={unique}");
        } else {
            parameters.put("unique", false);
            sb.append("&unique={unique}");
        }

        return get("/stats?" + sb, null, parameters);
    }
}
