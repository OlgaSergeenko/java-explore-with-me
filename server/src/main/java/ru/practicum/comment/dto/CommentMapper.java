package ru.practicum.comment.dto;

import ru.practicum.admin.users.UserShortDto;
import ru.practicum.comment.Comment;
import ru.practicum.event.dto.EventIdTitleDto;

import java.time.LocalDateTime;

public class CommentMapper {

    public static Comment toComment(NewCommentDto commentDto) {
        return Comment.builder()
                .text(commentDto.getText())
                .event(null)
                .author(null)
                .creationDate(LocalDateTime.now())
                .isModified(false)
                .modificationDate(null)
                .respond(null)
                .build();
    }

    public static CommentWithRespondDto toDto(Comment comment) {
        return CommentWithRespondDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .event(EventIdTitleDto.builder().id(comment.getEvent().getId())
                        .title(comment.getEvent().getTitle()).build())
                .author(UserShortDto.builder().id(comment.getAuthor().getId())
                        .name(comment.getAuthor().getName()).build())
                .creationDate(comment.getCreationDate())
                .isModified(comment.isModified())
                .modificationDate(comment.getModificationDate())
                .respond(CommentDto.builder()
                        .id(comment.getRespond().getId())
                        .text(comment.getRespond().getText())
                        .author(UserShortDto.builder().id(comment.getRespond().getAuthor().getId())
                                .name(comment.getRespond().getAuthor().getName()).build())
                        .creationDate(comment.getRespond().getCreationDate())
                        .isModified(comment.getRespond().isModified())
                        .modificationDate(comment.getRespond().getModificationDate())
                        .build())
                .build();
    }

    public static CommentDto toDtoNoRespond(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .event(EventIdTitleDto.builder().id(comment.getEvent().getId())
                        .title(comment.getEvent().getTitle()).build())
                .author(UserShortDto.builder().id(comment.getAuthor().getId())
                        .name(comment.getAuthor().getName()).build())
                .creationDate(comment.getCreationDate())
                .isModified(comment.isModified())
                .modificationDate(comment.getModificationDate())
                .build();
    }
}
