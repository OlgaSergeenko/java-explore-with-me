package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Getter
@Setter
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public EndpointHitDto addHit(EndpointHitDto endpointHitDto) {
        return StatMapper.toDto(statsRepository.save(StatMapper.toHit(endpointHitDto)));
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris) {
        List<ViewStats> stats = statsRepository.countHitsByUri(start, end, uris);
        return stats.stream().map(StatMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ViewStatsDto> getStatsUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris) {
        List<ViewStats> stats = statsRepository.countHitsByUriWhereUniqueIps(start, end, uris);
        return stats.stream().map(StatMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ViewStatsDto> getStatsNoEndDate(LocalDateTime start, List<String> uris) {
        List<ViewStats> stats = statsRepository.countHitsByUriNoEndDate(start, uris);
        return stats.stream().map(StatMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ViewStatsDto> getStatsUniqueIpNoEndDate(LocalDateTime start, List<String> uris) {
        List<ViewStats> stats = statsRepository.countHitsByUriWhereUniqueIpsNoEndDate(start, uris);
        return stats.stream().map(StatMapper::toDto).collect(Collectors.toList());
    }
}
