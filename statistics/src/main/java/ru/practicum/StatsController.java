package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<EndpointHitDto> addHit(@RequestBody EndpointHitDto endpointHitDto) {
        EndpointHitDto saved = statsService.addHit(endpointHitDto);
        log.info("Saved hit");
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStatsDto>> getStats(@RequestParam(required = false) String start,
                                                       @RequestParam(required = false) String end,
                                                       @RequestParam(required = false, defaultValue = "false") String unique,
                                                       @RequestParam List<String> uris) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(start, formatter);
        if (end == null) {
            if (unique.equals("true")) {
                List<ViewStatsDto> stats = statsService.getStatsUniqueIpNoEndDate(startTime, uris);
                return ResponseEntity.ok(stats);
            }
            List<ViewStatsDto> stats = statsService.getStatsNoEndDate(startTime, uris);
            return ResponseEntity.ok(stats);
        }
        LocalDateTime endTime = LocalDateTime.parse(end, formatter);
        if (unique.equals("true")) {
            log.info("Getting stats with unique ip's");
            List<ViewStatsDto> stats = statsService.getStatsUniqueIp(startTime, endTime, uris);
            return ResponseEntity.ok(stats);
        }
        log.info("Getting stats");
        List<ViewStatsDto> stats = statsService.getStats(startTime, endTime, uris);
        return ResponseEntity.ok(stats);
    }
}
