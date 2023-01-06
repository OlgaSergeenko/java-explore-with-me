package ru.practicum.publicUser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicGatewayController {

    private final PublicUserClient publicUserClient;

    @GetMapping("/events")
    public ResponseEntity<Object> findAllEvents(@RequestParam(required = false) String text,
                                                @RequestParam(required = false) List<Integer> categories,
                                                @RequestParam(required = false) Boolean paid,
                                                @RequestParam(required = false) String rangeStart,
                                                @RequestParam(required = false) String rangeEnd,
                                                @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                                @RequestParam(required = false) EventSortParam sort,
                                                @RequestParam(required = false, defaultValue = "0") Integer from,
                                                @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Public: Getting all events");

        return publicUserClient.findAllEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<Object> getEventInfo(@PathVariable("eventId") long eventId,
                                               HttpServletRequest request) {
        log.info("Public: Getting event id {}", eventId);
        return publicUserClient.getEventInfo(eventId);
    }

    @GetMapping("/categories")
    public ResponseEntity<Object> findAllCategories(@RequestParam(required = false, defaultValue = "0") Integer from,
                                                    @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Public: Getting all categories");
        return publicUserClient.findAllCategories(from, size);
    }

    @GetMapping("/categories/{catId}")
    public ResponseEntity<Object> findCategory(@PathVariable("catId") int catId) {
        log.info("Public: Getting category id {}", catId);
        return publicUserClient.findCategory(catId);
    }

    @GetMapping("/compilations")
    public ResponseEntity<Object> findAllCompilations(@RequestParam(required = false) Boolean pinned,
                                                      @RequestParam(required = false, defaultValue = "0") Integer from,
                                                      @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Public: Getting all compilations");
        return publicUserClient.findAllCompilations(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public ResponseEntity<Object> findCompilation(@PathVariable("compId") int compId) {
        log.info("Public: Getting compilation id {}", compId);
        return publicUserClient.findCompilation(compId);
    }
}
