package ru.practicum.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class EventRequestException extends RuntimeException {

    public EventRequestException(String message) {
        super(message);
    }
}
