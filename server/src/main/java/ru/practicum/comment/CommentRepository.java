package ru.practicum.comment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByIdAndEventIdAndAuthorId(long commentId, long eventId, long userId);

    Optional<Comment> findByIdAndEventId(long commentId, long eventId);

    Optional<Comment> findByRespondId(long commentId);

    List<Comment> findAllByEventIdAndReplyIsFalse(long eventId, Pageable pageable);

    List<Comment> findAllByIdIn(List<Long> ids);
}
