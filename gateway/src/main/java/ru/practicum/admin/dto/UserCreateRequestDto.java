package ru.practicum.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequestDto {
    @Email(message = "email format - xxx@xxx.ru")
    @NotBlank(message = "email required")
    private String email;
    @NotBlank(message = "name required")
    private String name;
}
