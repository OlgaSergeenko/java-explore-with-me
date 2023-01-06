package ru.practicum.request;

import ru.practicum.admin.users.User;
import ru.practicum.event.Event;

public class RequestMapper {

    public static ParticipationRequestDto toDto (ParticipationRequest request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
    }

    public static ParticipationRequest toRequest (ParticipationRequestDto request) {
        return ParticipationRequest.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(Event.builder().id(request.getId()).build())
                .requester(User.builder().id(request.getRequester()).build())
                .status(request.getStatus())
                .build();
    }
}
