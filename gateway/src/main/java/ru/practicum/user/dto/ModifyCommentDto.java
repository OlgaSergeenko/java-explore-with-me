package ru.practicum.user.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModifyCommentDto {
    @NotBlank
    private String text;
}
