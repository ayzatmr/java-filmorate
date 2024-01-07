package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.Optional;

public interface FilmDao {
    List<Film> findAllFilms();

    Film addFilm(Film film);

    Optional<Film> updateFilm(Film film);

    Optional<Film> addLike(int filmId, int userId);

    Optional<Film> deleteLike(int filmId, int userId);

    List<Film> getPopularFilms(int count);

    Optional<Film> getFilm(int filmId);

    Optional<Genre> getGenre(int genreId);

    List<Genre> getAllGenres();

    Optional<Rating> getRating(int ratingId);

    List<Rating> getAllRatings();
}
