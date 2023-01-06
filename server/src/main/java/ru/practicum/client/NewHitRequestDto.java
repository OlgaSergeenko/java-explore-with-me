package ru.practicum.client;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewHitRequestDto {
    private Long id;
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
