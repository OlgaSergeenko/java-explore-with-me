package ru.practicum.compilations;

public interface CompilationService {

    CompilationDto postNewCompilation(NewCompilationDto compilationDto);

    void removeCompilation(int compId);

    void removeEventFromCompilation(int compId, long eventId);

    void addEventToCompilation(int compId, long eventId);

    void pinCompilation(int compId);

    void unpinCompilation(int compId);
}
