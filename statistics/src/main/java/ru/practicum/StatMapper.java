package ru.practicum;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

public class StatMapper {

    public static EndpointHit toHit(EndpointHitDto dto) {
        return new EndpointHit(
                dto.getId(),
                dto.getApp(),
                dto.getUri(),
                dto.getIp(),
                dto.getTimestamp()
        );
    }

    public static EndpointHitDto toDto(EndpointHit endpointHit) {
        return new EndpointHitDto(
                endpointHit.getId(),
                endpointHit.getApp(),
                endpointHit.getUri(),
                endpointHit.getIp(),
                endpointHit.getTimestamp()
        );
    }

    public static ViewStatsDto toDto(ViewStats stats) {
        return new ViewStatsDto(
                stats.getApp(),
                stats.getUri(),
                stats.getHits()
        );
    }

    public static ViewStats toStats(ViewStatsDto dto) {
        return new ViewStats(
                dto.getApp(),
                dto.getUri(),
                dto.getHits()
        );
    }
}
