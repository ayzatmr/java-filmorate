package ru.yandex.practicum.filmorate.model;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public class ErrorResponse {
    private final String error;
    private final String description;
}
