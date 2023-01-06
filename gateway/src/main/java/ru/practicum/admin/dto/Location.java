package ru.practicum.admin.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Location {
    @NonNull
    private Float lat;
    @NonNull
    private Float lon;
}
