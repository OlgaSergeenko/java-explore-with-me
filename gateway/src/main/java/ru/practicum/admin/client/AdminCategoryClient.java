package ru.practicum.admin.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.admin.dto.CategoryCreateDto;
import ru.practicum.admin.dto.CategoryDto;
import ru.practicum.baseClient.BaseClient;

@Service
public class AdminCategoryClient extends BaseClient {

    private static final String API_PREFIX = "/admin/categories";

    @Autowired
    public AdminCategoryClient(@Value("${exploreWithMe-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> updateCategory(CategoryDto categoryRequestDto) {
        return patch("/", categoryRequestDto);
    }

    public ResponseEntity<Object> saveNewCategory(CategoryCreateDto categoryCreateDto) {
        return post("/", categoryCreateDto);
    }

    public ResponseEntity<Object> removeCategory(long catId) {
        return delete("/" + catId);
    }
}

