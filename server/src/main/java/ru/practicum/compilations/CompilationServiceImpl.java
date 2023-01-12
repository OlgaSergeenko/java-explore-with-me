package ru.practicum.compilations;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.Event;
import ru.practicum.event.EventRepository;
import ru.practicum.exceptions.CompilationNotFoundException;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.EventNotFoundException;

@Transactional(readOnly = true)
@Service
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CompilationDto postNewCompilation(NewCompilationDto compilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(compilationDto);
        compilation.setEvents(eventRepository.findAllByIds(compilationDto.getEvents()));
        Compilation saved = compilationRepository.save(compilation);

        return CompilationMapper.toDto(saved);
    }

    @Transactional
    @Override
    public void removeCompilation(int compId) {
        compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException("Compilation not found"));
        compilationRepository.deleteById(compId);
    }

    @Transactional
    @Override
    public void removeEventFromCompilation(int compId, long eventId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException("Compilation not found"));
        if (compilation.getEvents().stream().map(Event::getId).noneMatch(x -> x == eventId)) {
            throw new EventNotFoundException("No such event in compilation");
        }
        Event eventToRemove = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
        compilation.removeEvent(eventToRemove);
        compilationRepository.save(compilation);
    }

    @Transactional
    @Override
    public void addEventToCompilation(int compId, long eventId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException("Compilation not found"));
        if (compilation.getEvents().stream().map(Event::getId).anyMatch(x -> x == eventId)) {
            throw new ConflictException("Event is already in compilation");
        }
        Event eventToAdd = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
        compilation.addEvent(eventToAdd);
        compilationRepository.save(compilation);
    }

    @Transactional
    @Override
    public void pinCompilation(int compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException("Compilation not found"));
        compilation.setPinned(true);
        compilationRepository.save(compilation);
    }

    @Transactional
    @Override
    public void unpinCompilation(int compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException("Compilation not found"));
        compilation.setPinned(false);
        compilationRepository.save(compilation);
    }
}
