package ru.practicum.publicUser;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.admin.category.Category;
import ru.practicum.admin.category.CategoryDto;
import ru.practicum.admin.category.CategoryMapper;
import ru.practicum.admin.category.CategoryRepository;
import ru.practicum.compilations.Compilation;
import ru.practicum.compilations.CompilationDto;
import ru.practicum.compilations.CompilationMapper;
import ru.practicum.compilations.CompilationRepository;
import ru.practicum.enumerated.EventSortParam;
import ru.practicum.enumerated.EventState;
import ru.practicum.event.*;
import ru.practicum.event.dto.ConfirmedRequestCountByEvent;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.exceptions.CategoryNotFoundException;
import ru.practicum.exceptions.CompilationNotFoundException;
import ru.practicum.exceptions.EventNotFoundException;
import ru.practicum.request.RequestRepository;
import ru.practicum.util.QPredicates;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Transactional
@Service
@AllArgsConstructor
public class PublicUserServiceImpl implements PublicUserService {

    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getAllEvents(String text,
                                            List<Integer> categories,
                                            Boolean paid,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            boolean onlyAvailable,
                                            EventSortParam sortParam,
                                            Integer from,
                                            Integer size) {
        Pageable page = PageRequest.of(from / size, size);

        Predicate pr1 = QPredicates.builder()
                .add(text, QEvent.event.annotation::containsIgnoreCase)
                .add(text, QEvent.event.description::containsIgnoreCase)
                .buildOr();

        Predicate pr2 = QPredicates.builder()
                .add(categories, QEvent.event.category.id::in)
                .add(paid, QEvent.event.paid::eq)
                .add(rangeStart, QEvent.event.eventDate::after)
                .add(rangeEnd, QEvent.event.eventDate::before)
                .add(EventState.PUBLISHED, QEvent.event.state::eq)
                .buildAnd();

        Predicate predicate = ExpressionUtils.allOf(pr1, pr2);

        Iterable<Event> foundEvents1 = eventRepository.findAll(predicate, page);
        List<Event> events =
                StreamSupport.stream(foundEvents1.spliterator(), false)
                        .collect(Collectors.toList());

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .distinct()
                .collect(Collectors.toList());

        List<ConfirmedRequestCountByEvent> eventRequests = requestRepository.countConfirmedRequestsByEventIds(eventIds);

        events.forEach(e -> e.setConfirmedRequests(
                Math.toIntExact(eventRequests.stream()
                        .filter(x -> Objects.equals(x.getEventId(), e.getId()))
                        .map(ConfirmedRequestCountByEvent::getCount)
                        .findFirst().orElse(0L))));

        if (onlyAvailable) {
            events = events.stream()
                    .filter(e -> e.getConfirmedRequests() < e.getParticipantLimit() || e.getParticipantLimit() == 0)
                    .collect(Collectors.toList());
        }

        if (sortParam != null) {
            switch (sortParam) {
                case EVENT_DATE:
                    events = events.stream()
                            .sorted(Comparator.comparing(Event::getEventDate))
                            .collect(Collectors.toList());
                    break;
                case VIEWS:
                    events = events.stream()
                            .sorted(Comparator.comparing(Event::getViews))
                            .collect(Collectors.toList());
                    break;
                default:
                    throw new RuntimeException("No such sorting available");
            }
        }

        return events.stream().map(EventMapper::toShortDto).collect(Collectors.toList());
    }


    @Override
    public EventFullDto findEvent(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
        if (event.getState() != EventState.PUBLISHED) {
            throw new EventNotFoundException("Event is not published");
        }
        int confirmedReq = requestRepository.countConfirmedRequestsByEventId(eventId);
        event.setConfirmedRequests(confirmedReq);
        event.setViews(event.getViews() + 1);
        eventRepository.save(event);
        return EventMapper.toFullDto(event);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        Page<Category> categories = categoryRepository.findAll(page);
        return categories.getContent().stream().map(CategoryMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryDto getCategoryInfo(int catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        return CategoryMapper.toDto(category);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        Page<Compilation> compsPage;
        if (pinned == null) {
            compsPage = compilationRepository.findAll(page);
        } else {
            compsPage = compilationRepository.findAllByPinned(pinned, page);
        }
        List<Compilation> comps = compsPage.getContent();
        return comps.stream().map(CompilationMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public CompilationDto getCompilationInfo(int compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException("Compilation not found"));
        return CompilationMapper.toDto(compilation);
    }
}
