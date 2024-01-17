package ru.yandex.practicum.filmorate.dao.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmDao {
    List<Film> getAll();

    Film add(Film film);

    Optional<Film> update(Film film);

    Optional<Film> addLike(int filmId, int userId);

    Optional<Film> deleteLike(int filmId, int userId);

    List<Film> getPopularFilms(int count);

    Optional<Film> get(int filmId);
}
