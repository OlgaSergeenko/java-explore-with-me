package ru.practicum.user.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import ru.practicum.admin.users.User;
import ru.practicum.admin.users.UserService;
import ru.practicum.enumerated.EventState;
import ru.practicum.enumerated.RequestStatus;
import ru.practicum.event.Event;
import ru.practicum.exceptions.ParticipationRequestException;
import ru.practicum.exceptions.RequestNotFoundException;
import ru.practicum.request.ParticipationRequest;
import ru.practicum.request.ParticipationRequestDto;
import ru.practicum.request.RequestMapper;
import ru.practicum.request.RequestRepository;
import ru.practicum.user.events.UserEventService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Getter
@Setter
@AllArgsConstructor
public class UserRequestServiceImpl implements UserRequestService {

    private final UserEventService eventService;
    private final UserService userService;
    private final RequestRepository requestRepository;

    @Override
    public List<ParticipationRequestDto> getAllParticipationRequestsByUserId(long userId) {
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto postParticipationRequest(long userId, long eventId) {
        Optional<ParticipationRequest> requestCheck = requestRepository.findByRequesterIdAndEventId(userId, eventId);
        Event event = eventService.getEventByIdOrThrowNotFound(eventId);

        if (requestCheck.isPresent()) {
            throw new ParticipationRequestException("Request to this event is already sent.");
        }

        if (event.getInitiator().getId() == userId) {
            throw new ParticipationRequestException("Sending requests to your own events is not allowed.");
        }

        if (event.getState() == EventState.PENDING) {
            throw new ParticipationRequestException("Sending requests to unpublished events is not allowed.");
        }
        int confirmedReq = requestRepository.countRequestsByEventId(eventId);
        if (event.getParticipantLimit() == confirmedReq) {
            throw new ParticipationRequestException("Participant limit for this event is reached.");
        }

        ParticipationRequest request = new ParticipationRequest();
        User requester = userService.findByIdOrThrowNotFound(userId);

        request.setCreated(LocalDateTime.now().plusSeconds(3));
        request.setEvent(event);
        request.setRequester(requester);

        if (!event.getRequestModeration()) {
            request.setStatus(ru.practicum.enumerated.RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }
        return RequestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelParticipationRequest(long userId, long reqId) {
        Optional<ParticipationRequest> request = requestRepository.findById(reqId);
        if (request.isEmpty()) {
            throw new RequestNotFoundException("Request is not found");
        }
        ParticipationRequest toCancel = request.get();
        toCancel.setStatus(ru.practicum.enumerated.RequestStatus.CANCELED);

        return RequestMapper.toDto(requestRepository.save(toCancel));
    }
}
