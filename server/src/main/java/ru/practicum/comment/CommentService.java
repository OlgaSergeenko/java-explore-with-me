package ru.practicum.comment;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentWithRespondDto;
import ru.practicum.comment.dto.ModifyCommentDto;
import ru.practicum.comment.dto.NewCommentDto;

import java.util.List;

public interface CommentService {

    CommentDto postNewComment(long userId, long eventId, NewCommentDto commentDto);

    CommentDto editComment(long userId, long eventId, long commentId, ModifyCommentDto commentDto);

    void removeComment(long userId, long eventId, long commentId);

    CommentWithRespondDto replyToComment(long userId, long eventId, long commentId, NewCommentDto commentDto);

    List<CommentWithRespondDto> getAllEventComments(long eventId, Integer from, Integer size);

    void adminRemoveComment(long eventId, long commentId);
}
