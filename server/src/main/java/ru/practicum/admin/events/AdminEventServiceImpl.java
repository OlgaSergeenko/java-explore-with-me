package ru.practicum.admin.events;

import com.querydsl.core.types.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.admin.category.Category;
import ru.practicum.admin.category.CategoryRepository;
import ru.practicum.enumerated.EventState;
import ru.practicum.event.Event;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventRepository;
import ru.practicum.event.QEvent;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.exceptions.CategoryNotFoundException;
import ru.practicum.exceptions.EventNotFoundException;
import ru.practicum.exceptions.EventRequestException;
import ru.practicum.util.QPredicates;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Transactional(readOnly = true)
@Service
@AllArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<EventFullDto> getALlEvents(List<Long> users,
                                           List<EventState> states,
                                           List<Integer> categories,
                                           LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd,
                                           Integer from,
                                           Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        Predicate predicate = QPredicates.builder()
                .add(users, QEvent.event.initiator.id::in)
                .add(states, QEvent.event.state::in)
                .add(categories, QEvent.event.category.id::in)
                .add(rangeStart, QEvent.event.eventDate::after)
                .add(rangeEnd, QEvent.event.eventDate::before)
                .buildAnd();

        Iterable<Event> foundEvents = eventRepository.findAll(predicate, page);
        List<Event> result =
                StreamSupport.stream(foundEvents.spliterator(), false)
                        .collect(Collectors.toList());

        return result.stream().map(EventMapper::toFullDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto editEvent(long eventId, AdminUpdateEventRequestDto eventRequestDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        Optional.ofNullable(eventRequestDto.getAnnotation()).ifPresent(event::setAnnotation);
        if (Optional.ofNullable(eventRequestDto.getCategoryId()).isPresent()) {
            Category category = categoryRepository.findById(eventRequestDto.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
            event.setCategory(category);
        }
        Optional.ofNullable(eventRequestDto.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(eventRequestDto.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(eventRequestDto.getLocation()).ifPresent(event::setLocation);
        Optional.ofNullable(eventRequestDto.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(eventRequestDto.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(eventRequestDto.getRequestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(eventRequestDto.getTitle()).ifPresent(event::setTitle);

        return EventMapper.toFullDto(eventRepository.save(event));
    }

    @Transactional
    @Override
    public EventFullDto publishEvent(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new EventRequestException("Event is sooner than an hour.");
        }

        if (!event.getState().equals(EventState.PENDING)) {
            throw new EventRequestException("Event is not in a pending state.");
        }

        event.setState(EventState.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());
        return EventMapper.toFullDto(eventRepository.save(event));
    }

    @Transactional
    @Override
    public EventFullDto rejectEvent(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new EventRequestException("Published events can not be rejected.");
        }

        event.setState(EventState.CANCELED);
        return EventMapper.toFullDto(eventRepository.save(event));
    }
}
