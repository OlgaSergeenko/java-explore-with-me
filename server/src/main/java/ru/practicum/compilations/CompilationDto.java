package ru.practicum.compilations;

import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;
import ru.practicum.event.dto.EventShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompilationDto {
    @UniqueElements
    private List<EventShortDto> events;
    @NotNull
    private Integer id;
    @NotNull
    private Boolean pinned;
    @NotBlank
    private String title;
}
