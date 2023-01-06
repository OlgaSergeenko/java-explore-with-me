package ru.practicum.publicUser;

import ru.practicum.admin.category.CategoryDto;
import ru.practicum.compilations.CompilationDto;
import ru.practicum.enumerated.EventSortParam;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;

import java.time.LocalDateTime;
import java.util.List;

public interface PublicUserService {

    List<EventShortDto> getAllEvents(String text,
                                     List<Integer> categories,
                                     Boolean paid,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     Boolean onlyAvailable,
                                     EventSortParam sort,
                                     Integer from,
                                     Integer size);

    EventFullDto findEvent(long eventId);

    List<CategoryDto> getAllCategories(Integer from, Integer size);

    CategoryDto getCategoryInfo(int catId);

    List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationInfo(int compId);
}
