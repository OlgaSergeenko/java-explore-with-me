package ru.practicum.comment;

import lombok.*;
import ru.practicum.admin.users.User;
import ru.practicum.event.Event;

import javax.persistence.*;
import java.time.LocalDateTime;


@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comment")
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;
    @Column(name = "comment_text", nullable = false)
    private String text;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
    @Column(name = "is_modified")
    private boolean isModified; // marker to indicate that the comment has been modified
    @Column(name = "modification_date")
    private LocalDateTime modificationDate;
    @ManyToOne
    @JoinColumn(name = "response_comment_id")
    private Comment response;
    private boolean reply; // marker to indicate that the comment is a reply to another comment
}
