package ru.practicum.event;

import lombok.*;
import ru.practicum.admin.category.Category;
import ru.practicum.admin.users.User;
import ru.practicum.enumerated.EventState;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "event")
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;
    @Size(min = 20, max = 2000)
    @Column(nullable = false)
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @Column(name = "confirmed_requests")
    private Integer confirmedRequests;
    @Column(name = "creation_date")
    private LocalDateTime createdOn;
    @Size(min = 20, max = 7000)
    @Column(nullable = false)
    private String description;
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;
    @NotNull
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "lat", column = @Column(name = "lat")),
            @AttributeOverride(name = "lon", column = @Column(name = "lon"))
    })
    private Location location;
    @Column(name = "is_paid")
    private Boolean paid;
    @Column(name = "participant_limit")
    private Integer participantLimit;
    @Column(name = "publishing_date")
    private LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    @Size(min = 3, max = 120)
    @Column(nullable = false)
    private String title;
    @Enumerated(EnumType.STRING)
    private EventState state;
    private Integer views;
}