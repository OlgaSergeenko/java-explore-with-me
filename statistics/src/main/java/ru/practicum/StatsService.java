package ru.practicum;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    EndpointHitDto addHit(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    List<ViewStatsDto> getStatsUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);

    List<ViewStatsDto> getStatsNoEndDate(LocalDateTime start, List<String> uris);

    List<ViewStatsDto> getStatsUniqueIpNoEndDate(LocalDateTime start, List<String> uris);
}
