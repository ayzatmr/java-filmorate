package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private static final LocalDate MAX_DATE = LocalDate.of(1895, 12, 12);

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film getFilm(int filmId) {
        Film film = filmStorage.getFilm(filmId);
        if (film == null) {
            throw new ObjectNotFoundException("Film is not found");
        }
        return film;
    }

    public Film addFilm(Film film) {
        if (film.getReleaseDate().isBefore(MAX_DATE)) {
            throw new ValidationException("date can not be more than " + MAX_DATE.toString());
        }
        log.debug("add new film: {}", film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        if (film.getReleaseDate().isBefore(MAX_DATE)) {
            throw new ValidationException("date can not be more than " + MAX_DATE.toString());
        }
        log.debug("update film: {}", film);
        Film newFilm = filmStorage.updateFilm(film);
        if (newFilm == null) {
            throw new ObjectNotFoundException("Film not found");
        } else {
            return film;
        }
    }

    public Film addLike(int filmId, int userId) {
        log.debug("add like by userId = {} to film with id = {}", userId, filmId);
        Film film = filmStorage.addLike(filmId, userId);
        if (film == null) {
            throw new ObjectNotFoundException("Wrong film or userId is provided");
        }
        return film;
    }

    public void deleteLike(int filmId, int userId) {
        log.debug("delete like by userId = {} from film with id = {}", userId, filmId);
        Film film = filmStorage.deleteLike(filmId, userId);
        if (film == null) {
            throw new ObjectNotFoundException("Wrong film or userId is presented");
        }
    }

    public List<Film> getPopularFilms(int count) {
        log.debug("Get popular films with limit = {}", count);
        if (count <= 0) {
            throw new ValidationException("count should be greater than 0");
        }
        return filmStorage.getPopularFilms(count);
    }
}
