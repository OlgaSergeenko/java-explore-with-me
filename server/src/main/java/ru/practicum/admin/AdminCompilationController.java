package ru.practicum.admin;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.CompilationDto;
import ru.practicum.compilations.CompilationService;
import ru.practicum.compilations.NewCompilationDto;

@RestController
@RequestMapping("/admin/compilations")
@AllArgsConstructor
@Slf4j
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    public ResponseEntity<CompilationDto> createCompilation(@RequestBody NewCompilationDto compilationDto) {
        CompilationDto compilationSaved = compilationService.postNewCompilation(compilationDto);
        log.info(String.format("Compilation %s is posted", compilationSaved.getTitle()));
        return ResponseEntity.ok(compilationSaved);
    }

    @DeleteMapping("/{compId}")
    public long removeCompilation(@PathVariable Integer compId) {
        log.info("Remove comp {}", compId);
        compilationService.removeCompilation(compId);
        return compId;
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    public long removeEventFromCompilation(@PathVariable Integer compId,
                                           @PathVariable Long eventId) {
        log.info("Remove event {} from comp {}", eventId, compId);
        compilationService.removeEventFromCompilation(compId, eventId);
        return eventId;
    }

    @PatchMapping("/{compId}/events/{eventId}")
    public long addEventToCompilation(@PathVariable Integer compId,
                                      @PathVariable Long eventId) {
        log.info("Adding event {} to comp {}", eventId, compId);
        compilationService.addEventToCompilation(compId, eventId);
        return eventId;
    }

    @PatchMapping("/{compId}/pin")
    public long pinCompilation(@PathVariable Integer compId) {
        log.info("Pinning comp {}", compId);
        compilationService.pinCompilation(compId);
        return compId;
    }

    @DeleteMapping("/{compId}/pin")
    public long unpinCompilation(@PathVariable Integer compId) {
        log.info("Unpinning comp {}", compId);
        compilationService.unpinCompilation(compId);
        return compId;
    }
}
