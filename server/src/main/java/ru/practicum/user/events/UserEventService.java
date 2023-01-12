package ru.practicum.user.events;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventDto;
import ru.practicum.request.ParticipationRequestDto;

import java.util.List;

public interface UserEventService {
    List<EventShortDto> getAllUserEvents(int userId, int from, int size);

    EventFullDto updateEvent(long userId, UpdateEventDto updateEventDto);

    EventFullDto postNewEvent(long userId, NewEventDto newEventDto);

    EventFullDto getEventById(long userId, long eventId);

    EventFullDto cancelEventById(long userId, long eventId);

    List<ParticipationRequestDto> getRequestsByEventId(long userId, long eventId);

    ParticipationRequestDto confirmRequest(long userId, long eventId, long reqId);

    ParticipationRequestDto rejectRequest(long userId, long eventId, long reqId);
}
