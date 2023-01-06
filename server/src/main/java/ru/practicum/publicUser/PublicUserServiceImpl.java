package ru.practicum.publicUser;

import com.querydsl.core.types.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.admin.category.Category;
import ru.practicum.admin.category.CategoryDto;
import ru.practicum.admin.category.CategoryMapper;
import ru.practicum.admin.category.CategoryService;
import ru.practicum.admin.events.AdminEventService;
import ru.practicum.compilations.Compilation;
import ru.practicum.compilations.CompilationDto;
import ru.practicum.compilations.CompilationMapper;
import ru.practicum.compilations.CompilationService;
import ru.practicum.enumerated.EventSortParam;
import ru.practicum.enumerated.EventState;
import ru.practicum.event.Event;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventRepository;
import ru.practicum.event.QEvent;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.exceptions.EventNotFoundException;
import ru.practicum.util.QPredicates;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class PublicUserServiceImpl implements PublicUserService {

    private final EventRepository eventRepository;
    private final AdminEventService eventService;
    private final CompilationService compilationService;
    private final CategoryService categoryService;

    @Override
    public List<EventShortDto> getAllEvents(String text,
                                            List<Integer> categories,
                                            Boolean paid,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            Boolean onlyAvailable,
                                            EventSortParam sortParam,
                                            Integer from,
                                            Integer size) {
        List<Event> result = new ArrayList<>();
        Pageable page = PageRequest.of(from / size, size);

        Predicate predicate1 = QPredicates.builder()
                .add(text, QEvent.event.annotation::containsIgnoreCase)
                .add(categories, QEvent.event.category.id::in)
                .add(paid, QEvent.event.paid::eq)
                .add(rangeStart, QEvent.event.eventDate::after)
                .add(rangeEnd, QEvent.event.eventDate::before)
                .add(EventState.PUBLISHED, QEvent.event.state::eq)
                .buildAnd();

        Predicate predicate2 = QPredicates.builder()
                .add(text, QEvent.event.description::containsIgnoreCase)
                .add(categories, QEvent.event.category.id::in)
                .add(paid, QEvent.event.paid::eq)
                .add(rangeStart, QEvent.event.eventDate::after)
                .add(rangeEnd, QEvent.event.eventDate::before)
                .add(EventState.PUBLISHED, QEvent.event.state::eq)
                .buildAnd();

        Iterable<Event> foundEvents1 = eventRepository.findAll(predicate1, page);
        Iterable<Event> foundEvents2 = eventRepository.findAll(predicate2, page);
        List<Event> ev1 =
                StreamSupport.stream(foundEvents1.spliterator(), false)
                        .collect(Collectors.toList());
        List<Event> ev2 =
                StreamSupport.stream(foundEvents2.spliterator(), false)
                        .collect(Collectors.toList());

        ev1.addAll(ev2);

        List<Event> events = ev1.stream().distinct().collect(Collectors.toList());

        if (onlyAvailable) {
            List<Event> events1 = events.stream()
                    .filter(e -> e.getConfirmedRequests() < e.getParticipantLimit())
                    .collect(Collectors.toList());
            List<Event> events2 = events.stream()
                    .filter(e -> e.getParticipantLimit() == 0)
                    .collect(Collectors.toList());

            result.addAll(events1);
            result.addAll(events2);
        } else {
            result = events;
        }

        if (sortParam != null) {
            switch (sortParam) {
                case EVENT_DATE:
                    result = result.stream()
                            .sorted(Comparator.comparing(Event::getEventDate))
                            .collect(Collectors.toList());
                    break;
                case VIEWS:
                    result = result.stream()
                            .sorted(Comparator.comparing(Event::getViews))
                            .collect(Collectors.toList());
                    break;
                default:
                    throw new RuntimeException("No such sorting available");
            }
        }

        return result.stream().map(EventMapper::toShortDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto findEvent(long eventId) {
        Event event = eventService.getEventByIdOrThrowNotFound(eventId);
        if (event.getState() != EventState.PUBLISHED) {
            throw new EventNotFoundException("Event is not published");
        }
        event.setViews(event.getViews() + 1);
        eventRepository.save(event);
        return EventMapper.toFullDto(event);
    }

    @Override
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        return categoryService.findAll(page);
    }

    @Override
    public CategoryDto getCategoryInfo(int catId) {
        Category category = categoryService.getCategoryByIdOrThrowNotFound(catId);
        return CategoryMapper.toDto(category);
    }

    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        return compilationService.findCompilations(pinned, page);
    }

    @Override
    public CompilationDto getCompilationInfo(int compId) {
        Compilation compilation = compilationService.findByIdOrThrowNotFound(compId);
        return CompilationMapper.toDto(compilation);
    }
}
