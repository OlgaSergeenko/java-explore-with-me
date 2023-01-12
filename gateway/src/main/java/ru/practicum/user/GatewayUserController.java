package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.client.EventClient;
import ru.practicum.user.client.RequestClient;
import ru.practicum.user.dto.NewEventDto;
import ru.practicum.user.dto.UpdateEventRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class GatewayUserController {

    private final EventClient userClient;
    private final RequestClient requestClient;

    @GetMapping("/{userId}/events")
    public ResponseEntity<Object> getEventsByUserId(@Positive @PathVariable Long userId,
                                                    @RequestParam(name = "from", required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                                    @RequestParam(name = "size", required = false, defaultValue = "10") @PositiveOrZero Integer size) {
        log.info("Get all events for user {}", userId);
        return userClient.getEventsByUserId(userId, from, size);
    }

    @PostMapping("/{userId}/events")
    public ResponseEntity<Object> saveNewEvent(@Valid @RequestBody NewEventDto newEventDto,
                                               @Positive @PathVariable Long userId) {
        log.info("Creating event {}", newEventDto);
        return userClient.createNewEvent(userId, newEventDto);
    }

    @PatchMapping("/{userId}/events")
    public ResponseEntity<Object> updateEvent(@Valid @RequestBody UpdateEventRequestDto eventRequestDto,
                                              @Positive @PathVariable Long userId) {
        log.info("Updating event id - {}", eventRequestDto.getEventId());
        return userClient.updateEvent(userId, eventRequestDto);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public ResponseEntity<Object> getEventByUserIdAndEventId(@Positive @PathVariable Long userId,
                                                             @Positive @PathVariable Long eventId) {
        log.info("Get events id - {} for user id - {}", eventId, userId);
        return userClient.getEventByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public ResponseEntity<Object> cancelEvent(@Positive @PathVariable Long userId,
                                              @Positive @PathVariable Long eventId) {
        log.info("Cancelling event id - {}", eventId);
        return userClient.cancelEvent(userId, eventId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<Object> findAllRequestsByEventId(@PathVariable("userId") @NotNull Long userId,
                                                           @PathVariable("eventId") @NotNull Long eventId) {
        log.info(String.format("Getting requests for event id - %d", eventId));
        return requestClient.getAllRequestsByEventId(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public ResponseEntity<Object> confirmRequest(@PathVariable("userId") Long userId,
                                                 @PathVariable("eventId") Long eventId,
                                                 @PathVariable("reqId") Long reqId) {
        log.info(String.format("Confirming the request id - %d", reqId));

        return requestClient.confirmRequest(userId, eventId, reqId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/reject")
    public ResponseEntity<Object> rejectRequest(@PathVariable("userId") Long userId,
                                                @PathVariable("eventId") Long eventId,
                                                @PathVariable("reqId") Long reqId) {
        log.info(String.format("Rejecting the request id - %d", reqId));

        return requestClient.rejectRequest(userId, eventId, reqId);
    }

    @PostMapping("/{userId}/requests")
    public ResponseEntity<Object> postNewRequest(@RequestParam("eventId") @Positive Long eventId,
                                                 @PathVariable("userId") @Positive Long userId) {
        log.info("Posting new request");
        return requestClient.postNewRequest(userId, eventId);
    }

    @GetMapping("/{userId}/requests")
    public ResponseEntity<Object> findAllUserRequests(@PathVariable("userId") Long userId) {
        log.info("Getting all user requests");
        return requestClient.findAllUserRequests(userId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<Object> cancelRequest(@PathVariable("userId") Long userId,
                                                @PathVariable("requestId") Long reqId) {
        log.info("Canceling the requests");
        return requestClient.cancelRequest(userId, reqId);
    }
}
