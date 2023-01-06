package ru.practicum.user.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.baseClient.BaseClient;


@Service
public class RequestClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    @Autowired
    public RequestClient(@Value("${exploreWithMe-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getAllRequestsByEventId(long userId, long eventId) {
        return get("/" + userId + "/events/" + eventId + "/requests");
    }

    public ResponseEntity<Object> confirmRequest(long userId, long eventId, long reqId) {
        return patch("/" + userId + "/events/" + eventId + "/requests/" + reqId + "/confirm");
    }

    public ResponseEntity<Object> rejectRequest(long userId, long eventId, long reqId) {
        return patch("/" + userId + "/events/" + eventId + "/requests/" + reqId + "/reject");
    }

    public ResponseEntity<Object> postNewRequest(long userId, long eventId) {
        return post("/" + userId + "/requests?eventId=" + eventId, null);
    }

    public ResponseEntity<Object> findAllUserRequests(long userId) {
        return get("/" + userId + "/requests");
    }

    public ResponseEntity<Object> cancelRequest(long userId, long reqId) {
        return patch("/" + userId + "/requests/" + reqId + "/cancel");
    }
}
