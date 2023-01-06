package ru.practicum.publicUser;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.admin.category.CategoryDto;
import ru.practicum.client.NewHitRequestDto;
import ru.practicum.client.StatClient;
import ru.practicum.compilations.CompilationDto;
import ru.practicum.enumerated.EventSortParam;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class PublicServerController {

    private final PublicUserService userService;

    private final StatClient statClient;

    @GetMapping("/events")
    public ResponseEntity<List<EventShortDto>> findAllEvents(HttpServletRequest request,
                                                             @RequestParam(required = false) String text,
                                                             @RequestParam(required = false) List<Integer> categories,
                                                             @RequestParam(required = false) Boolean paid,
                                                             @RequestParam(required = false) String rangeStart,
                                                             @RequestParam(required = false) String rangeEnd,
                                                             @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                                             @RequestParam(required = false) EventSortParam sort,
                                                             @RequestParam Integer from,
                                                             @RequestParam Integer size) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime;
        LocalDateTime endTime = null;
        if (rangeStart != null) {
            startTime = LocalDateTime.parse(rangeStart, formatter);
        } else {
            startTime = LocalDateTime.now();
        }
        if (rangeEnd != null) {
            endTime = LocalDateTime.parse(rangeEnd, formatter);
        }

        statClient.saveNewHit(NewHitRequestDto.builder()
                .app("ewm-main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now().format(formatter))
                .build());

        return ResponseEntity.ok(userService.getAllEvents(text, categories, paid, startTime, endTime, onlyAvailable, sort, from, size));
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<EventFullDto> getEventInfo(@PathVariable("eventId") long eventId,
                                                     HttpServletRequest request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        EventFullDto event = userService.findEvent(eventId);

        String dateTime = String.valueOf(LocalDateTime.now());
        try {
            URLEncoder.encode(dateTime, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }

        statClient.saveNewHit(NewHitRequestDto.builder()
                .app("ewm-main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now().format(formatter))
                .build());

        return ResponseEntity.ok(event);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> findAllCategories(@RequestParam Integer from,
                                                               @RequestParam Integer size) {
        List<CategoryDto> categories = userService.getAllCategories(from, size);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/categories/{catId}")
    public ResponseEntity<CategoryDto> findCategory(@PathVariable("catId") int catId) {
        CategoryDto category = userService.getCategoryInfo(catId);
        return ResponseEntity.ok(category);
    }

    @GetMapping("/compilations")
    public ResponseEntity<List<CompilationDto>> findAllCompilations(@RequestParam(required = false) Boolean pinned,
                                                                    @RequestParam Integer from,
                                                                    @RequestParam Integer size) {
        List<CompilationDto> compilations = userService.getAllCompilations(pinned, from, size);
        return ResponseEntity.ok(compilations);
    }

    @GetMapping("/compilations/{compId}")
    public ResponseEntity<CompilationDto> findCompilation(@PathVariable("compId") int compId) {
        CompilationDto compilation = userService.getCompilationInfo(compId);
        return ResponseEntity.ok(compilation);
    }
}
