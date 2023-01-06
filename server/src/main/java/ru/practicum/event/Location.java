package ru.practicum.event;

import lombok.*;

import javax.persistence.Embeddable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Location {
    @NonNull
    private Float lat;
    @NonNull
    private Float lon;
}
