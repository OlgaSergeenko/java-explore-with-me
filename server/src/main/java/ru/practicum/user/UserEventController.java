package ru.practicum.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventDto;
import ru.practicum.request.ParticipationRequestDto;
import ru.practicum.user.events.UserEventService;

import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@Slf4j
public class UserEventController {

    private final UserEventService userEventService;


    @PostMapping("/{userId}/events")
    public ResponseEntity<EventFullDto> postNewEvent(@RequestBody NewEventDto newEventDto,
                                                     @PathVariable("userId") Integer userId) {
        EventFullDto eventSaved = userEventService.postNewEvent(userId, newEventDto);
        log.info(String.format("Event with id %d is posted", eventSaved.getId()));
        return ResponseEntity.ok(eventSaved);
    }

    @GetMapping("/{userId}/events")
    public ResponseEntity<List<EventShortDto>> findAllEventsByUserId(@PathVariable("userId") Integer userId,
                                                                     @RequestParam Integer from,
                                                                     @RequestParam Integer size) {
        return ResponseEntity.ok(userEventService.getAllUserEvents(userId, from, size));
    }

    @PatchMapping("/{userId}/events")
    public ResponseEntity<EventFullDto> updateEventInfo(@PathVariable("userId") Integer userId,
                                                        @RequestBody UpdateEventDto updateEventDto) {
        log.info(String.format("Updating event with id - %d", updateEventDto.getEventId()));
        EventFullDto updatedEvent = userEventService.updateEvent(userId, updateEventDto);
        return ResponseEntity.ok(updatedEvent);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> findEventById(@PathVariable("userId") Integer userId,
                                                      @PathVariable("eventId") Integer eventId) {
        log.info(String.format("Searching for the event with id - %d", eventId));
        return ResponseEntity.ok(userEventService.getEventById(userId, eventId));
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> cancelEvent(@PathVariable("userId") Integer userId,
                                                    @PathVariable("eventId") Integer eventId) {
        log.info(String.format("Cancelling event with id - %d", eventId));
        EventFullDto cancelled = userEventService.cancelEventById(userId, eventId);
        return ResponseEntity.ok(cancelled);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> findAllRequestsByEventId(@PathVariable("userId") Long userId,
                                                                                  @PathVariable("eventId") Long eventId) {
        log.info(String.format("Getting requests for event id - %d", eventId));
        return ResponseEntity.ok(userEventService.getRequestsByEventId(userId, eventId));
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public ResponseEntity<ParticipationRequestDto> confirmRequest(@PathVariable("userId") Long userId,
                                                                  @PathVariable("eventId") Long eventId,
                                                                  @PathVariable("reqId") Long reqId) {
        log.info(String.format("Confirming the request id - %d", reqId));

        return ResponseEntity.ok(userEventService.confirmRequest(userId, eventId, reqId));
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/reject")
    public ResponseEntity<ParticipationRequestDto> rejectRequest(@PathVariable("userId") Integer userId,
                                                                 @PathVariable("eventId") Integer eventId,
                                                                 @PathVariable("reqId") Integer reqId) {
        log.info(String.format("Rejecting the request id - %d", reqId));

        return ResponseEntity.ok(userEventService.rejectRequest(userId, eventId, reqId));
    }
}
