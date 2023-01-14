package ru.practicum.event.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventIdTitleDto {
    private Long id;
    private String title;
}
