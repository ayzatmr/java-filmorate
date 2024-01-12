package ru.yandex.practicum.filmorate.dao.interfaces;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedHashSet;
import java.util.Optional;

public interface GenreDao {
    Optional<Genre> getGenre(int genreId);

    LinkedHashSet<Genre> getAllGenres();

    Film addFilmGenre(Film film);

    void deleteFilmGenre(Film film);
}
