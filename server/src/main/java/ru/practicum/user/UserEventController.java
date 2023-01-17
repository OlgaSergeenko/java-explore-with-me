package ru.practicum.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.CommentService;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentWithRespondDto;
import ru.practicum.comment.dto.ModifyCommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventDto;
import ru.practicum.request.ParticipationRequestDto;
import ru.practicum.user.events.UserEventService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@AllArgsConstructor
@Slf4j
public class UserEventController {

    private final UserEventService userEventService;

    private final CommentService commentService;


    @PostMapping
    public ResponseEntity<EventFullDto> postNewEvent(@RequestBody NewEventDto newEventDto,
                                                     @PathVariable("userId") Long userId) {
        EventFullDto eventSaved = userEventService.postNewEvent(userId, newEventDto);
        log.info(String.format("Event with id %d is posted", eventSaved.getId()));
        return ResponseEntity.ok(eventSaved);
    }

    @GetMapping
    public ResponseEntity<List<EventShortDto>> findAllEventsByUserId(@PathVariable("userId") Long userId,
                                                                     @RequestParam Integer from,
                                                                     @RequestParam Integer size) {
        return ResponseEntity.ok(userEventService.getAllUserEvents(userId, from, size));
    }

    @PatchMapping
    public ResponseEntity<EventFullDto> updateEventInfo(@PathVariable("userId") Long userId,
                                                        @RequestBody UpdateEventDto updateEventDto) {
        log.info(String.format("Updating event with id - %d", updateEventDto.getEventId()));
        EventFullDto updatedEvent = userEventService.updateEvent(userId, updateEventDto);
        return ResponseEntity.ok(updatedEvent);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> findEventById(@PathVariable("userId") Long userId,
                                                      @PathVariable("eventId") Long eventId) {
        log.info(String.format("Searching for the event with id - %d", eventId));
        return ResponseEntity.ok(userEventService.getEventById(userId, eventId));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> cancelEvent(@PathVariable("userId") Long userId,
                                                    @PathVariable("eventId") Long eventId) {
        log.info(String.format("Cancelling event with id - %d", eventId));
        EventFullDto cancelled = userEventService.cancelEventById(userId, eventId);
        return ResponseEntity.ok(cancelled);
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> findAllRequestsByEventId(@PathVariable("userId") Long userId,
                                                                                  @PathVariable("eventId") Long eventId) {
        log.info(String.format("Getting requests for event id - %d", eventId));
        return ResponseEntity.ok(userEventService.getRequestsByEventId(userId, eventId));
    }

    @PatchMapping("/{eventId}/requests/{reqId}/confirm")
    public ResponseEntity<ParticipationRequestDto> confirmRequest(@PathVariable("userId") Long userId,
                                                                  @PathVariable("eventId") Long eventId,
                                                                  @PathVariable("reqId") Long reqId) {
        log.info(String.format("Confirming the request id - %d", reqId));

        return ResponseEntity.ok(userEventService.confirmRequest(userId, eventId, reqId));
    }

    @PatchMapping("/{eventId}/requests/{reqId}/reject")
    public ResponseEntity<ParticipationRequestDto> rejectRequest(@PathVariable("userId") Long userId,
                                                                 @PathVariable("eventId") Long eventId,
                                                                 @PathVariable("reqId") Integer reqId) {
        log.info(String.format("Rejecting the request id - %d", reqId));

        return ResponseEntity.ok(userEventService.rejectRequest(userId, eventId, reqId));
    }

    @PostMapping("/{eventId}/comments")
    public ResponseEntity<CommentDto> postNewComment(@RequestBody NewCommentDto commentDto,
                                                     @PathVariable("userId") Long userId,
                                                     @PathVariable("eventId") Long eventId) {
        CommentDto commentSaved = commentService.postNewComment(userId, eventId, commentDto);
        log.info(String.format("Comment with id %d is posted", commentSaved.getId()));
        return ResponseEntity.ok(commentSaved);
    }

    @PostMapping("/{eventId}/comments/{commentId}/reply")
    public ResponseEntity<CommentWithRespondDto> addReplyToComment(@PathVariable("userId") Long userId,
                                                                   @PathVariable("eventId") Long eventId,
                                                                   @PathVariable("commentId") Integer commentId,
                                                                   @RequestBody NewCommentDto commentDto) {
        log.info(String.format("Replying to comment id - %d", commentId));
        return ResponseEntity.ok(commentService.replyToComment(userId, eventId, commentId, commentDto));
    }

    @PatchMapping("/{eventId}/comments/{commentId}")
    public ResponseEntity<CommentDto> modifyComment(@PathVariable("userId") Long userId,
                                                    @PathVariable("eventId") Long eventId,
                                                    @PathVariable("commentId") Integer commentId,
                                                    @RequestBody ModifyCommentDto commentDto) {
        log.info(String.format("Editing comment id - %d", commentId));
        return ResponseEntity.ok(commentService.editComment(userId, eventId, commentId, commentDto));
    }

    @GetMapping("/{eventId}/comments")
    public ResponseEntity<List<CommentWithRespondDto>> getAllCommentsByEventId(@PathVariable("eventId") Long eventId,
                                                                               @RequestParam Integer from,
                                                                               @RequestParam Integer size) {
        return ResponseEntity.ok(commentService.getAllEventComments(eventId, from, size));
    }

    @DeleteMapping("/{eventId}/comments/{commentId}")
    public long removeComment(@PathVariable("userId") Long userId,
                                                    @PathVariable("eventId") Long eventId,
                                                    @PathVariable("commentId") Integer commentId) {
        log.info(String.format("Removing comment id - %d", commentId));
        commentService.removeComment(userId, eventId, commentId);
        return commentId;
    }
}
