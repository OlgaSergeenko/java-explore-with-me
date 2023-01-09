package ru.practicum.compilations;

import ru.practicum.event.EventMapper;

import java.util.Collections;
import java.util.stream.Collectors;

public class CompilationMapper {

    public static Compilation toCompilation(NewCompilationDto compilationDto) {
        return Compilation.builder()
                .id(compilationDto.getId())
                .events(Collections.emptyList())
                .pinned(compilationDto.isPinned())
                .title(compilationDto.getTitle())
                .build();
    }

    public static CompilationDto toDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(compilation.getEvents().stream().map(EventMapper::toShortDto).collect(Collectors.toList()))
                .pinned(compilation.isPinned())
                .title(compilation.getTitle())
                .build();
    }
}
