package ru.practicum.user.events;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.admin.category.Category;
import ru.practicum.admin.category.CategoryRepository;
import ru.practicum.admin.users.User;
import ru.practicum.admin.users.UserRepository;
import ru.practicum.enumerated.EventState;
import ru.practicum.enumerated.RequestStatus;
import ru.practicum.event.Event;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventDto;
import ru.practicum.exceptions.*;
import ru.practicum.request.ParticipationRequest;
import ru.practicum.request.ParticipationRequestDto;
import ru.practicum.request.RequestMapper;
import ru.practicum.request.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@AllArgsConstructor
public class UserEventServiceImpl implements UserEventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;

    @Override
    public List<EventShortDto> getAllUserEvents(long userId, int from, int size) {
        Pageable page = PageRequest.of(from / size, size);
        return eventRepository.findAllByAndInitiatorId(userId, page).stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto updateEvent(long userId, UpdateEventDto updateEventDto) {
        if (updateEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EventRequestException("Event date is too soon.");
        }
        Optional<Event> eventToUpdate = eventRepository.findByIdAndInitiatorId(updateEventDto.getEventId(), userId);
        if (eventToUpdate.isEmpty()) {
            throw new EventNotFoundException("Only initiator can update event");
        }
        Event event = eventToUpdate.get();
        if (event.getState() == EventState.PUBLISHED) {
            throw new EventRequestException("You cannot update published events");
        }

        Optional.ofNullable(updateEventDto.getAnnotation()).ifPresent(event::setAnnotation);
        if (Optional.ofNullable(updateEventDto.getCategoryId()).isPresent()) {
            Category category = categoryRepository.findById(updateEventDto.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
            event.setCategory(category);
        }
        Optional.ofNullable(updateEventDto.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(updateEventDto.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(updateEventDto.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(updateEventDto.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updateEventDto.getTitle()).ifPresent(event::setTitle);

        event.setState(EventState.PENDING);

        Event saved = eventRepository.save(event);

        return EventMapper.toFullDto(saved);
    }

    @Transactional
    @Override
    public EventFullDto postNewEvent(long userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EventRequestException("Event date is too soon.");
        }
        Event event = EventMapper.toEvent(newEventDto);

        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        event.setInitiator(initiator);

        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        event.setCategory(category);

        if (event.getParticipantLimit() == null) {
            event.setParticipantLimit(10);
        }
        if (event.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }

        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
        event.setViews(0);

        return EventMapper.toFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEventById(long userId, long eventId) {
        Optional<Event> eventFound = eventRepository.findById(eventId);
        if (eventFound.isEmpty()) {
            throw new EventNotFoundException("Event not found");
        }
        int confirmedRequests = requestRepository.countConfirmedRequestsByEventId(eventId);
        Event event = eventFound.get();
        event.setConfirmedRequests(confirmedRequests);
        return EventMapper.toFullDto(event);
    }

    @Transactional
    @Override
    public EventFullDto cancelEventById(long userId, long eventId) {
        Optional<Event> eventToCancel = eventRepository.findByIdAndInitiatorId(eventId, userId);
        if (eventToCancel.isEmpty()) {
            throw new EventNotFoundException("Only initiator can cancel the event");
        }
        Event event = eventToCancel.get();
        if (event.getState() != EventState.PENDING) {
            throw new EventNotFoundException("Only PENDING events can be canceled");
        }
        event.setState(EventState.CANCELED);
        return EventMapper.toFullDto(eventRepository.save(event));
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByEventId(long userId, long eventId) {
        List<ParticipationRequest> requests = requestRepository.findAllByEventId_AndEvent_InitiatorId(eventId, userId);
        return requests.stream().map(RequestMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ParticipationRequestDto confirmRequest(long userId, long eventId, long reqId) {
        ParticipationRequest request = requestRepository.findById(reqId)
                .orElseThrow(() -> new RequestNotFoundException("Request not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
        if (event.getInitiator().getId() != userId) {
            throw new EventRequestException("Only initiator can confirm request");
        }
        if (request.getEvent().getId() != eventId) {
            throw new EventRequestException("Event in the request does not match the eventId");
        }

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
            requestRepository.save(request);
            return RequestMapper.toDto(request);
        }
        int confirmedRequests = requestRepository.countConfirmedRequestsByEventId(eventId);
        if (confirmedRequests == event.getParticipantLimit()) {
            request.setStatus(RequestStatus.REJECTED);
            requestRepository.save(request);
            return RequestMapper.toDto(request);
        }

        request.setStatus(RequestStatus.CONFIRMED);
        ParticipationRequest requestSaved = requestRepository.save(request);
        return RequestMapper.toDto(requestSaved);
    }

    @Transactional
    @Override
    public ParticipationRequestDto rejectRequest(long userId, long eventId, long reqId) {
        ParticipationRequest request = requestRepository.findByIdAndEventId_AndEvent_InitiatorId(reqId, eventId, userId)
                .orElseThrow(() -> new RequestNotFoundException("Request not found"));

        request.setStatus(RequestStatus.REJECTED);
        ParticipationRequest requestSaved = requestRepository.save(request);
        return RequestMapper.toDto(requestSaved);
    }
}
