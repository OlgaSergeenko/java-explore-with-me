package ru.practicum.admin.events;

import ru.practicum.enumerated.EventState;
import ru.practicum.event.dto.AdminUpdateEventRequestDto;
import ru.practicum.event.dto.EventFullDto;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminEventService {

    List<EventFullDto> getALlEvents(List<Long> users,
                                    List<EventState> states,
                                    List<Integer> categories,
                                    LocalDateTime rangeStart,
                                    LocalDateTime rangeEnd,
                                    Integer from,
                                    Integer size);

    EventFullDto editEvent(long eventId, AdminUpdateEventRequestDto eventRequestDto);

    EventFullDto publishEvent(long eventId);

    EventFullDto rejectEvent(long eventId);
}
