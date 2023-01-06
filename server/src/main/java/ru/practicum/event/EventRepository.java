package ru.practicum.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    List<Event> findAllByAndInitiatorId(long id, Pageable pageable);

    @Query(value = "select e " +
            "from Event as e " +
            "where e.id in ?1")
    List<Event> findAllByIds(List<Long> ids);

    List<Event> findAllByCompilationId(int compId);
}
