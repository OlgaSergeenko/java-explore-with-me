package ru.practicum.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.event.dto.ConfirmedRequestCountByEvent;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByEventId_AndEvent_InitiatorId(long eventId, long userId);

    Optional<ParticipationRequest> findByIdAndEventId_AndEvent_InitiatorId(long reqId, long eventId, long userId);

    @Query("select count(r.id) " +
            "from ParticipationRequest as r " +
            "where r.event.id = ?1 " +
            "and r.status = 'CONFIRMED' ")
    int countConfirmedRequestsByEventId(long eventId);

    @Query(value = "select new ru.practicum.event.dto.ConfirmedRequestCountByEvent(r.event.id, count (r.id)) " +
            "from ParticipationRequest as r " +
            "where r.event.id in ?1 " +
            "and r.status = 'CONFIRMED' " +
            "group by r.event.id ")
    List<ConfirmedRequestCountByEvent> countConfirmedRequestsByEventIds(List<Long> eventIds);

    List<ParticipationRequest> findAllByRequesterId(long userId);

    Optional<ParticipationRequest> findByRequesterIdAndEventId(long userId, long eventId);

    Optional<ParticipationRequest> findByIdAndRequesterId(long reqId, long userId);
}
