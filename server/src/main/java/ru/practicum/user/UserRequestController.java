package ru.practicum.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.ParticipationRequestDto;
import ru.practicum.user.requests.UserRequestService;

import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@Slf4j
public class UserRequestController {

    private final UserRequestService requestService;

    @PostMapping("/{userId}/requests")
    public ResponseEntity<ParticipationRequestDto> postNewRequest(@RequestParam("eventId") Long eventId,
                                                                  @PathVariable("userId") Long userId) {
        ParticipationRequestDto requestSaved = requestService.postParticipationRequest(userId, eventId);
        log.info(String.format("Request with id %d is posted", requestSaved.getId()));
        return ResponseEntity.ok(requestSaved);
    }

    @GetMapping("/{userId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> findAllUserRequests(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(requestService.getAllParticipationRequestsByUserId(userId));
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(@PathVariable("userId") Long userId,
                                                                 @PathVariable("requestId") Long reqId) {
        log.info(String.format("Cancelling the request with id - %d", reqId));
        ParticipationRequestDto requestCanceled = requestService.cancelParticipationRequest(userId, reqId);
        return ResponseEntity.ok(requestCanceled);
    }
}
