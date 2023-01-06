package ru.practicum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Integer>, QuerydslPredicateExecutor<ViewStats> {

    @Query(value = "select new ru.practicum.model.ViewStats(h.app, h.uri, count(h.uri)) " +
            "from EndpointHit as h " +
            "where h.timestamp between ?1 and ?2 " +
            "and h.uri in ( ?3 ) " +
            "group by h.uri, h.app")
    public List<ViewStats> countHitsByUri(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "select new ru.practicum.model.ViewStats(h.app, h.uri, count(distinct h.ip)) " +
            "from EndpointHit as h " +
            "where h.timestamp between ?1 and ?2 " +
            "and h.uri in ( ?3 ) " +
            "group by h.uri, h.app")
    public List<ViewStats> countHitsByUriWhereUniqueIps(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "select new ru.practicum.model.ViewStats(h.app, h.uri, count(distinct h.ip)) " +
            "from EndpointHit as h " +
            "where h.timestamp >= ?1 " +
            "and h.uri in ( ?2 ) " +
            "group by h.uri, h.app")
    public List<ViewStats> countHitsByUriWhereUniqueIpsNoEndDate(LocalDateTime start, List<String> uris);

    @Query(value = "select new ru.practicum.model.ViewStats(h.app, h.uri, count(h.uri)) " +
            "from EndpointHit as h " +
            "where h.timestamp >= ?1 " +
            "and h.uri in ( ?2 ) " +
            "group by h.uri, h.app")
    public List<ViewStats> countHitsByUriNoEndDate(LocalDateTime start, List<String> uris);
}
