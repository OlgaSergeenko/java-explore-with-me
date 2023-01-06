package ru.practicum.compilations;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.admin.events.AdminEventService;
import ru.practicum.event.Event;
import ru.practicum.event.EventRepository;
import ru.practicum.exceptions.CompilationNotFoundException;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.EventNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final AdminEventService eventService;

    @Override
    public CompilationDto postNewCompilation(NewCompilationDto compilationDto) {
        if (compilationDto.getPinned() == null) {
            compilationDto.setPinned(false);
        }
        Compilation compilation = CompilationMapper.toCompilation(compilationDto);
        List<Event> events = eventRepository.findAllByIds(compilationDto.getEvents());
        Compilation saved = compilationRepository.save(compilation);

        saved.setEvents(events);
        events.forEach(e -> e.setCompilation(saved));
        eventRepository.saveAll(events);

        return CompilationMapper.toDto(saved);
    }

    @Override
    public void removeCompilation(int compId) {
        Compilation compilation = findByIdOrThrowNotFound(compId);
        List<Event> events = compilation.getEvents();
        events.forEach(e -> e.setCompilation(null));
        eventRepository.saveAll(events);
        compilationRepository.deleteById(compId);
    }

    @Override
    public void removeEventFromCompilation(int compId, long eventId) {
        Compilation compilation = findByIdOrThrowNotFound(compId);
        if (compilation.getEvents().stream().map(Event::getId).noneMatch(x -> x == eventId)) {
            throw new EventNotFoundException("No such event in compilation");
        }
        Event eventToRemove = eventService.getEventByIdOrThrowNotFound(eventId);
        compilation.removeEvent(eventToRemove);
        compilationRepository.save(compilation);
    }

    @Override
    public void addEventToCompilation(int compId, long eventId) {
        Compilation compilation = findByIdOrThrowNotFound(compId);
        if (compilation.getEvents().stream().map(Event::getId).anyMatch(x -> x == eventId)) {
            throw new ConflictException("Event is already in compilation");
        }
        List<Event> events = compilation.getEvents();
        Event eventToAdd = eventService.getEventByIdOrThrowNotFound(eventId);
        compilation.addEvent(eventToAdd);
        compilationRepository.save(compilation);
    }

    @Override
    public void pinCompilation(int compId) {
        Compilation compilation = findByIdOrThrowNotFound(compId);
        compilation.setPinned(true);
        compilationRepository.save(compilation);
    }

    @Override
    public void unpinCompilation(int compId) {
        Compilation compilation = findByIdOrThrowNotFound(compId);
        compilation.setPinned(false);
        compilationRepository.save(compilation);
    }

    @Override
    public List<CompilationDto> findCompilations(Boolean pinned, Pageable page) {
        Page<Compilation> compsPage;
        if (pinned == null) {
            compsPage = compilationRepository.findAll(page);
        } else if (pinned) {
            compsPage = compilationRepository.findAllByPinnedIsTrue(page);
        } else {
            compsPage = compilationRepository.findAllByPinnedIsFalse(page);
        }
        List<Compilation> comps = compsPage.getContent();
        return comps.stream().map(CompilationMapper::toDto).collect(Collectors.toList());
    }

    public Compilation findByIdOrThrowNotFound(int compId) {
        Optional<Compilation> compSearch = compilationRepository.findById(compId);
        if (compSearch.isEmpty()) {
            throw new CompilationNotFoundException("Compilation in not found.");
        }
        List<Event> eventsInComp = eventRepository.findAllByCompilationId(compId);
        Compilation compilation = compSearch.get();
        compilation.setEvents(eventsInComp);
        return compilation;
    }
}
