package ru.practicum.user.requests;

import ru.practicum.request.ParticipationRequestDto;

import java.util.List;

public interface UserRequestService {

    List<ParticipationRequestDto> getAllParticipationRequestsByUserId(long userId);

    ParticipationRequestDto postParticipationRequest(long userId, long eventId);

    ParticipationRequestDto cancelParticipationRequest(long userId, long reqId);
}
