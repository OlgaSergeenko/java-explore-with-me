package ru.practicum.admin;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.admin.events.AdminEventService;
import ru.practicum.event.dto.AdminUpdateEventRequestDto;
import ru.practicum.comment.CommentService;
import ru.practicum.comment.dto.CommentWithRespondDto;
import ru.practicum.enumerated.EventState;
import ru.practicum.event.dto.EventFullDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@AllArgsConstructor
@Slf4j
public class AdminEventController {

    private final AdminEventService eventService;
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<EventFullDto>> getAllEventsByParams(@RequestParam(required = false) List<Long> users,
                                                                   @RequestParam(required = false) List<EventState> states,
                                                                   @RequestParam(required = false) List<Integer> categories,
                                                                   @RequestParam(required = false) String rangeStart,
                                                                   @RequestParam(required = false) String rangeEnd,
                                                                   @RequestParam Integer from,
                                                                   @RequestParam Integer size) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (rangeStart != null) {
            startTime = LocalDateTime.parse(rangeStart, formatter);
        }
        if (rangeStart != null) {
            endTime = LocalDateTime.parse(rangeEnd, formatter);
        }
        if (users.get(0) == 0) {
            users = null;
        }
        if (categories.get(0) == 0) {
            categories = null;
        }
        return ResponseEntity.ok(
                eventService.getALlEvents(users, states, categories, startTime, endTime, from, size));
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@PathVariable("eventId") long eventId,
                                                    @RequestBody AdminUpdateEventRequestDto eventRequestDto) {
        return ResponseEntity.ok(eventService.editEvent(eventId, eventRequestDto));
    }

    @PatchMapping("/{eventId}/publish")
    public ResponseEntity<EventFullDto> publishEvent(@PathVariable("eventId") long eventId) {
        return ResponseEntity.ok(eventService.publishEvent(eventId));
    }

    @PatchMapping("/{eventId}/reject")
    public ResponseEntity<EventFullDto> rejectEvent(@PathVariable("eventId") long eventId) {
        return ResponseEntity.ok(eventService.rejectEvent(eventId));
    }

    @DeleteMapping("/{eventId}/comments/{commentId}")
    public long removeComment(@PathVariable("eventId") Long eventId,
                              @PathVariable("commentId") Integer commentId) {
        log.info(String.format("Admin removing comment id - %d", commentId));
        commentService.adminRemoveComment(eventId, commentId);
        return commentId;
    }

    @GetMapping("/{eventId}/comments")
    public ResponseEntity<List<CommentWithRespondDto>> getAllCommentsByEventId(@PathVariable("eventId") Long eventId,
                                                                               @RequestParam Integer from,
                                                                               @RequestParam Integer size) {
        return ResponseEntity.ok(commentService.getAllEventComments(eventId, from, size));
    }
}
