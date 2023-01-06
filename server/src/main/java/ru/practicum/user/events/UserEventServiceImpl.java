package ru.practicum.user.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.admin.category.Category;
import ru.practicum.admin.category.CategoryRepository;
import ru.practicum.admin.category.CategoryService;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Getter
@Setter
@AllArgsConstructor
public class UserEventServiceImpl implements UserEventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;
    private final RequestRepository requestRepository;

    @Override
    public List<EventShortDto> getAllUserEvents(int userId, int from, int size) {
        Pageable page = PageRequest.of(from / size, size);
        return eventRepository.findAllByAndInitiatorId(userId, page).stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEvent(long userId, UpdateEventDto updateEventDto) {
        if (updateEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EventRequestException("Event date is too soon.");
        }
        Optional<Event> eventToUpdate = eventRepository.findById(updateEventDto.getEventId());
        if (eventToUpdate.isEmpty()) {
            throw new EventNotFoundException("Event not found");
        }
        Event event = eventToUpdate.get();
        if (event.getState() == EventState.PUBLISHED) {
            throw new EventRequestException("You cannot update published events");
        }

        Optional.ofNullable(updateEventDto.getAnnotation()).ifPresent(event::setAnnotation);
        if (Optional.ofNullable(updateEventDto.getCategoryId()).isPresent()) {
            Category category = categoryService.getCategoryByIdOrThrowNotFound(updateEventDto.getCategoryId());
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

    @Override
    public EventFullDto postNewEvent(long userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EventRequestException("Event date is too soon.");
        }
        Event event = EventMapper.toEvent(newEventDto);

        Optional<User> initiator = userRepository.findById((long) userId);
        if (initiator.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        event.setInitiator(initiator.get());

        Optional<Category> category = categoryRepository.findById(newEventDto.getCategory());
        if (category.isEmpty()) {
            throw new CategoryNotFoundException("Category not found");
        }
        event.setCategory(category.get());

        if (event.getPaid() == null) {
            event.setPaid(false);
        }
        if (event.getParticipantLimit() == null) {
            event.setParticipantLimit(10);
        }
        if (event.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }

        event.setPublishedOn(LocalDateTime.now());
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
        int confirmedRequests = requestRepository.countRequestsByEventId(eventId);
        Event event = eventFound.get();
        event.setConfirmedRequests(confirmedRequests);
        return EventMapper.toFullDto(event);
    }

    @Override
    public EventFullDto cancelEventById(long userId, long eventId) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            throw new EventNotFoundException("Event not found");
        }
        Event toCancel = event.get();
        toCancel.setState(EventState.CANCELED);
        return EventMapper.toFullDto(eventRepository.save(toCancel));
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByEventId(long userId, long eventId) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            throw new EventNotFoundException("Event not found");
        }
        List<ParticipationRequest> requests = requestRepository.findAllByEventId_AndEvent_InitiatorId(eventId, userId);
        if (requests.isEmpty()) {
            return Collections.emptyList();
        }
        return requests.stream().map(RequestMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto confirmRequest(long userId, long eventId, long reqId) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            throw new EventNotFoundException("Event not found");
        }
        Event eventToConfirm = event.get();
        Optional<ParticipationRequest> req = requestRepository.findById(reqId);
        ParticipationRequest request = req.orElseThrow(() -> new RequestNotFoundException("Request is not found"));
        if (!eventToConfirm.getRequestModeration() || eventToConfirm.getParticipantLimit() == 0) {
            return RequestMapper.toDto(request);
        }

        request.setStatus(ru.practicum.enumerated.RequestStatus.CONFIRMED);
        eventToConfirm.setConfirmedRequests(eventToConfirm.getConfirmedRequests() + 1);
        eventRepository.save(eventToConfirm);
        ParticipationRequest requestSaved = requestRepository.save(request);
        return RequestMapper.toDto(requestSaved);
    }

    @Override
    public ParticipationRequestDto rejectRequest(long userId, long eventId, long reqId) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            throw new EventNotFoundException("Event not found");
        }
        Optional<ParticipationRequest> req = requestRepository.findById(reqId);
        ParticipationRequest request = req.orElseThrow(() -> new RequestNotFoundException("Request is not found"));
        request.setStatus(RequestStatus.REJECTED);
        ParticipationRequest requestSaved = requestRepository.save(request);
        return RequestMapper.toDto(requestSaved);
    }

    @Override
    public Event getEventByIdOrThrowNotFound(long eventId) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            throw new EventNotFoundException("Event not found");
        }
        return event.get();
    }
}
