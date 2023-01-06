package ru.practicum.admin.client;

import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.admin.dto.UserCreateRequestDto;
import ru.practicum.baseClient.BaseClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminUserClient extends BaseClient {

    private static final String API_PREFIX = "/admin/users";

    @Autowired
    public AdminUserClient(@Value("${exploreWithMe-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getUsers(List<Long> ids, Integer from, Integer size) {
        if (ids != null) {
            Map<String, Object> parameters = Map.of(
                    "ids", StringUtils.join(ids.stream().map(String::valueOf).collect(Collectors.toList())),
                    "from", from,
                    "size", size
            );
            return get("?ids={ids}&from={from}&size={size}", null, parameters);
        }
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", null, parameters);
    }

    public ResponseEntity<Object> saveNewUser(UserCreateRequestDto userDto) {
        return post("/", userDto);
    }

    public ResponseEntity<Object> removeUser(long userId) {
        return delete("/" + userId);
    }
}

