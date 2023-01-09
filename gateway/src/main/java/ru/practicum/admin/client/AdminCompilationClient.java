package ru.practicum.admin.client;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.admin.dto.NewCompilationDto;
import ru.practicum.baseClient.BaseClient;

@Service
public class AdminCompilationClient extends BaseClient {

    private static final String API_PREFIX = "/admin/compilations";

    @Autowired
    public AdminCompilationClient(@Value("${exploreWithMe-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> postNewCompilation(NewCompilationDto compilationDto) {
        return post("", compilationDto);
    }

    public ResponseEntity<Object> removeCompilation(int compId) {
        return delete("/" + compId);
    }

    public ResponseEntity<Object> addEventToCompilation(int compId, long eventId) {
        return patch("/" + compId + "/events/" + eventId, compId, eventId);
    }

    public ResponseEntity<Object> removeEventFromCompilation(int compId, long eventId) {
        return delete("/" + compId + "/events/" + eventId);
    }

    public ResponseEntity<Object> pinCompilation(int compId) {
        return patch("/" + compId + "/pin", compId);
    }

    public ResponseEntity<Object> unpinCompilation(int compId) {
        return delete("/" + compId + "/pin");
    }
}