package ru.practicum.user.requests;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.admin.users.User;
import ru.practicum.admin.users.UserRepository;
import ru.practicum.enumerated.EventState;
import ru.practicum.enumerated.RequestStatus;
import ru.practicum.event.Event;
import ru.practicum.event.EventRepository;
import ru.practicum.exceptions.EventNotFoundException;
import ru.practicum.exceptions.ParticipationRequestException;
import ru.practicum.exceptions.RequestNotFoundException;
import ru.practicum.exceptions.UserNotFoundException;
import ru.practicum.request.ParticipationRequest;
import ru.practicum.request.ParticipationRequestDto;
import ru.practicum.request.RequestMapper;
import ru.practicum.request.RequestRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@AllArgsConstructor
public class UserRequestServiceImpl implements UserRequestService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    @Override
    public List<ParticipationRequestDto> getAllParticipationRequestsByUserId(long userId) {
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ParticipationRequestDto postParticipationRequest(long userId, long eventId) {
        Optional<ParticipationRequest> requestCheck = requestRepository.findByRequesterIdAndEventId(userId, eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

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
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        request.setEvent(event);
        request.setRequester(requester);

        if (!event.getRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }
        return RequestMapper.toDto(requestRepository.save(request));
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelParticipationRequest(long userId, long reqId) {
        ParticipationRequest request = requestRepository.findByIdAndRequesterId(reqId, userId)
                .orElseThrow(() -> new RequestNotFoundException("Request is not found"));
        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.toDto(requestRepository.save(request));
    }
}
