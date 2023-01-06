package ru.practicum.compilations;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CompilationService {

    CompilationDto postNewCompilation(NewCompilationDto compilationDto);

    void removeCompilation(int compId);

    void removeEventFromCompilation(int compId, long eventId);

    void addEventToCompilation(int compId, long eventId);

    void pinCompilation(int compId);

    void unpinCompilation(int compId);

    List<CompilationDto> findCompilations(Boolean pinned, Pageable page);

    Compilation findByIdOrThrowNotFound(int compId);
}
