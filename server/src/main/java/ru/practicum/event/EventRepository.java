package ru.practicum.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    List<Event> findAllByAndInitiatorId(long id, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(long eventId, long userId);

    @Query(value = "select e " +
            "from Event as e " +
            "where e.id in ?1")
    List<Event> findAllByIds(List<Long> ids);

    List<Event> findAllByCategoryId(int catId);
}
