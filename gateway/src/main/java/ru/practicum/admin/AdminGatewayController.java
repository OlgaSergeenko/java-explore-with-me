package ru.practicum.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.admin.client.AdminCategoryClient;
import ru.practicum.admin.client.AdminCompilationClient;
import ru.practicum.admin.client.AdminEventClient;
import ru.practicum.admin.client.AdminUserClient;
import ru.practicum.admin.dto.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Controller
@RequestMapping(path = "/admin")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminGatewayController {

    private final AdminUserClient adminClient;
    private final AdminCategoryClient categoryClient;
    private final AdminEventClient eventClient;
    private final AdminCompilationClient compilationClient;

    @GetMapping("/users")
    public ResponseEntity<Object> getUsers(@RequestParam(required = false) List<Long> ids,
                                           @RequestParam(required = false, defaultValue = "0") Integer from,
                                           @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Get all users request");
        return adminClient.getUsers(ids, from, size);
    }

    @PostMapping("/users")
    public ResponseEntity<Object> saveUser(@Valid @RequestBody UserCreateRequestDto userDto) {
        log.info("Creating user {}", userDto);
        return adminClient.saveNewUser(userDto);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Object> removeUser(@PathVariable("id") Long userId) {
        log.info("Remove user {}", userId);
        return adminClient.removeUser(userId);
    }

    @PatchMapping("/categories")
    public ResponseEntity<Object> updateCategory(@Valid @RequestBody CategoryDto categoryRequestDto) {
        log.info("Updating category {}, id = {}", categoryRequestDto.getName(), categoryRequestDto.getId());
        return categoryClient.updateCategory(categoryRequestDto);
    }

    @PostMapping("/categories")
    public ResponseEntity<Object> saveCategory(@Valid @RequestBody CategoryCreateDto categoryCreateDto) {
        log.info("Creating category {}", categoryCreateDto);
        return categoryClient.saveNewCategory(categoryCreateDto);
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Object> removeCategory(@PathVariable("id") Long catId) {
        log.info("Remove category {}", catId);
        return categoryClient.removeCategory(catId);
    }

    @GetMapping("/events")
    public ResponseEntity<Object> getAllEventsByParams(@RequestParam(required = false) List<Long> users,
                                                       @RequestParam(required = false) List<String> states,
                                                       @RequestParam(required = false) List<Integer> categories,
                                                       @RequestParam(required = false) String rangeStart,
                                                       @RequestParam(required = false) String rangeEnd,
                                                       @RequestParam(required = false, defaultValue = "0") Integer from,
                                                       @RequestParam(required = false, defaultValue = "10") Integer size) {
        return eventClient.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PutMapping("/events/{eventId}")
    public ResponseEntity<Object> updateEvent(@PathVariable("eventId") @Positive long eventId,
                                              @RequestBody @Valid AdminUpdateEventRequestDto eventRequestDto) {
        return eventClient.updateEvent(eventId, eventRequestDto);
    }

    @PatchMapping("/events/{eventId}/publish")
    public ResponseEntity<Object> publishEvent(@PathVariable("eventId") @Positive long eventId) {
        return eventClient.publishEvent(eventId);
    }

    @PatchMapping("/events/{eventId}/reject")
    public ResponseEntity<Object> rejectEvent(@PathVariable("eventId") @Positive long eventId) {
        return eventClient.rejectEvent(eventId);
    }

    @PostMapping("/compilations")
    public ResponseEntity<Object> createCompilation(@RequestBody @Valid NewCompilationDto compilationDto) {
        log.info("posting new compilation {}", compilationDto.getTitle());
        return compilationClient.postNewCompilation(compilationDto);
    }

    @DeleteMapping("/compilations/{compId}")
    public ResponseEntity<Object> removeCompilation(@PathVariable @Positive Integer compId) {
        log.info("Removing comp {}", compId);
        return compilationClient.removeCompilation(compId);
    }

    @DeleteMapping("/compilations/{compId}/events/{eventId}")
    public ResponseEntity<Object> removeEventFromCompilation(@PathVariable @Positive Integer compId,
                                                             @PathVariable @Positive Long eventId) {
        log.info("Removing event {} from comp {}", eventId, compId);
        return compilationClient.removeEventFromCompilation(compId, eventId);
    }

    @PatchMapping("/compilations/{compId}/events/{eventId}")
    public ResponseEntity<Object> addEventToCompilation(@PathVariable Integer compId,
                                                        @PathVariable Long eventId) {
        log.info("Adding event {} to comp {}", eventId, compId);
        return compilationClient.addEventToCompilation(compId, eventId);
    }

    @PatchMapping("/compilations/{compId}/pin")
    public ResponseEntity<Object> pinCompilation(@PathVariable Integer compId) {
        log.info("Pinning comp {}", compId);
        return compilationClient.pinCompilation(compId);
    }

    @DeleteMapping("/compilations/{compId}/pin")
    public ResponseEntity<Object> unpinCompilation(@PathVariable Integer compId) {
        log.info("Unpinning comp {}", compId);
        return compilationClient.unpinCompilation(compId);
    }

    @DeleteMapping("/events/{eventId}/comments/{commentId}")
    public long removeComment(@PathVariable("eventId") Long eventId,
                              @PathVariable("commentId") Integer commentId) {
        log.info(String.format("Admin removing comment id - %d", commentId));
        eventClient.removeComment(eventId, commentId);
        return commentId;
    }

    @GetMapping("/events/{eventId}/comments")
    public ResponseEntity<Object> getAllCommentsByEventId(@PathVariable("eventId") Long eventId,
                                                          @RequestParam(required = false, defaultValue = "0") Integer from,
                                                          @RequestParam(required = false, defaultValue = "10") Integer size) {
        return eventClient.getComments(eventId, from, size);
    }
}
