package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> findAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film addLike(int filmId, int userId);

    Film deleteLike(int filmId, int userId);

    List<Film> getPopularFilms(int count);

    Film getFilm(int filmId);
}
