package ru.practicum.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.admin.users.User;
import ru.practicum.admin.users.UserRepository;
import ru.practicum.admin.users.UserShortDto;
import ru.practicum.comment.dto.*;
import ru.practicum.enumerated.EventState;
import ru.practicum.event.Event;
import ru.practicum.event.EventRepository;
import ru.practicum.event.dto.EventIdTitleDto;
import ru.practicum.exceptions.CommentNotFoundException;
import ru.practicum.exceptions.EventNotFoundException;
import ru.practicum.exceptions.OperationNotAllowedException;
import ru.practicum.exceptions.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public CommentDto postNewComment(long userId, long eventId, NewCommentDto commentDto) {
        Comment comment = CommentMapper.toComment(commentDto);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
        if (event.getState() != EventState.PUBLISHED) {
            throw new OperationNotAllowedException("Event should be PUBLISHED to leave a comment");
        }
        comment.setEvent(event);
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        comment.setAuthor(author);

        return CommentMapper.toDtoNoRespond(commentRepository.save(comment));
    }

    @Override
    public CommentDto editComment(long userId, long eventId, long commentId, ModifyCommentDto commentDto) {
        Optional<Comment> commentToModify = commentRepository.findByIdAndEventIdAndAuthorId(commentId, eventId, userId);
        if (commentToModify.isEmpty()) {
            throw new CommentNotFoundException("Comment not found");
        }
        Comment comment = commentToModify.get();
        if (comment.getResponse() != null) {
            throw new OperationNotAllowedException("Only comment without event owner reply can be edited");
        }

        comment.setText(commentDto.getText());
        comment.setModified(true);
        comment.setModificationDate(LocalDateTime.now());

        return CommentMapper.toDtoNoRespond(commentRepository.save(comment));
    }

    @Override
    public void removeComment(long userId, long eventId, long commentId) {
        Optional<Comment> commentToRemove = commentRepository.findByIdAndEventIdAndAuthorId(commentId, eventId, userId);
        if (commentToRemove.isEmpty()) {
            throw new CommentNotFoundException("Only comment author can remove comment");
        }
        Comment comment = commentToRemove.get();
        if (comment.getResponse() != null) {
            throw new OperationNotAllowedException("Only comment without event owner reply can be removed");
        }

        Optional<Comment> isRespond = commentRepository.findByResponseId(commentId);
        if (isRespond.isPresent()) {
            throw new OperationNotAllowedException("Owner respond can not be removed");
        }

        commentRepository.deleteById(commentId);
    }

    @Override
    public CommentWithRespondDto replyToComment(long userId, long eventId, long commentId, NewCommentDto commentDto) {
        Comment reply = CommentMapper.toComment(commentDto);

        Optional<Comment> commentToReply = commentRepository.findByIdAndEventId(commentId, eventId);
        if (commentToReply.isEmpty()) {
            throw new CommentNotFoundException("Comment not found");
        }
        Comment comment = commentToReply.get();

        if (comment.getResponse() != null) {
            throw new OperationNotAllowedException("Reply to this comment is already posted");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
        User replyAuthor = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!Objects.equals(event.getInitiator().getId(), replyAuthor.getId())) {
            throw new OperationNotAllowedException("Only event initiator can reply to comments");
        }
        reply.setEvent(event);
        reply.setAuthor(replyAuthor);
        reply.setReply(true);
        commentRepository.save(reply);
        comment.setResponse(reply);

        return CommentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentWithRespondDto> getAllEventComments(long eventId, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        List<Comment> comments = commentRepository.findAllByEventIdAndReplyIsFalse(eventId, page);

        return comments.stream()
                .map(c -> c.getResponse() != null ? CommentMapper.toDto(c)
                        : new CommentWithRespondDto(
                        c.getId(),
                        c.getText(),
                        EventIdTitleDto.builder().id(c.getEvent().getId())
                                .title(c.getEvent().getTitle()).build(),
                        UserShortDto.builder().id(c.getAuthor().getId())
                                .name(c.getAuthor().getName()).build(),
                        c.getCreationDate(),
                        c.isModified(),
                        c.getModificationDate(),
                        null))
                        .collect(Collectors.toList());
    }

    @Override
    public void adminRemoveComment(long eventId, long commentId) {
        Optional<Comment> commentToRemove = commentRepository.findByIdAndEventId(commentId, eventId);
        if (commentToRemove.isEmpty()) {
            throw new CommentNotFoundException("Comment not found");
        }
        Comment comment = commentToRemove.get();
        if (comment.getResponse() != null) {
            commentRepository.deleteById(comment.getResponse().getId());
        }
        commentRepository.deleteById(commentId);
    }
}
