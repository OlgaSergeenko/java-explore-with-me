package ru.practicum.compilations;

import lombok.*;
import ru.practicum.event.Event;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "compilation")
@Builder
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compilation_id")
    private Integer id;
    private Boolean pinned;
    @NotNull
    private String title;
    @OneToMany(mappedBy = "compilation")
    @ToString.Exclude
    private List<Event> events = new ArrayList<>();

    public void addEvent(Event event) {
        events.add(event);
        event.setCompilation(this);
    }

    public void removeEvent(Event event) {
        events.remove(event);
        event.setCompilation(null);
    }
}
