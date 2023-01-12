package ru.practicum.user.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Location {
    private Long id;
    @NotNull
    private float lat;
    @NotNull
    private float lon;
}
