package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> findAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film addLike(Film film);

    void deleteLike(Film film);

    List<Film> getLikedFilms(int count);

    Film getFilm(int filmId);
}
