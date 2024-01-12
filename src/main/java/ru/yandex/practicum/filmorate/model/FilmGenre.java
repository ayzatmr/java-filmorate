package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;


@Data
@Jacksonized
@AllArgsConstructor
@NoArgsConstructor
public class FilmGenre {
    private int filmId;

    private int id;

    private String name;

    public Genre toGenre() {
        return new Genre(id, name);
    }
}



