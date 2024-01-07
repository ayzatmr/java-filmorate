package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmDao filmDao;

    private final UserDao userDao;

    private static final LocalDate MAX_DATE = LocalDate.of(1895, 12, 12);

    public FilmService(@Qualifier("FilmDaoImpl") FilmDao filmDao, @Qualifier("UserDaoImpl") UserDao userDao) {
        this.filmDao = filmDao;
        this.userDao = userDao;
    }

    private Film leftUniqueGenres(Film film) {
        if (film.getGenres() != null) {
            List<Genre> genres = film.getGenres()
                    .stream()
                    .distinct()
                    .collect(Collectors.toList());
            film.setGenres(genres);
        }
        return film;
    }

    private void checkFilmMaxDate(Film film) {
        if (film.getReleaseDate().isBefore(MAX_DATE)) {
            throw new ValidationException("date can not be more than " + MAX_DATE.toString());
        }
    }

    public List<Film> findAllFilms() {
        return filmDao.findAllFilms();
    }

    public Film getFilm(int filmId) {
        return filmDao.getFilm(filmId)
                .orElseThrow(() -> new ObjectNotFoundException("Film is not found"));
    }

    public Film addFilm(Film film) {
        checkFilmMaxDate(film);
        Film updatedFilm = leftUniqueGenres(film);
        log.debug("add new film: {}", updatedFilm);
        return filmDao.addFilm(updatedFilm);
    }

    public Film updateFilm(Film film) {
        checkFilmMaxDate(film);
        Film updatedFilm = leftUniqueGenres(film);
        log.debug("update film: {}", updatedFilm);
        return filmDao.updateFilm(updatedFilm)
                .orElseThrow(() -> new ObjectNotFoundException("Film is not found"));
    }

    public Film addLike(int filmId, int userId) {
        log.debug("add like by userId = {} to film with id = {}", userId, filmId);
        if (userDao.getUser(userId).isEmpty()) {
            throw new ObjectNotFoundException("User not found");
        }
        return filmDao.addLike(filmId, userId)
                .orElseThrow(() -> new ObjectNotFoundException("Wrong film or userId is provided"));
    }

    public void deleteLike(int filmId, int userId) {
        log.debug("delete like by userId = {} from film with id = {}", userId, filmId);
        if (userDao.getUser(userId).isEmpty()) {
            throw new ObjectNotFoundException("User not found");
        }
        filmDao.deleteLike(filmId, userId)
                .orElseThrow(() -> new ObjectNotFoundException("Wrong film or userId is presented"));
    }

    public List<Film> getPopularFilms(int count) {
        log.debug("Get popular films with limit = {}", count);
        return filmDao.getPopularFilms(count);
    }

    public List<Rating> getAllRatings() {
        log.debug("get all available ratings");
        return filmDao.getAllRatings();
    }

    public Rating getRatingById(int ratingId) {
        log.debug("get rating with id = {}", ratingId);
        return filmDao.getRating(ratingId)
                .orElseThrow(() -> new ObjectNotFoundException("Rating is not found"));
    }

    public List<Genre> getAllGenres() {
        log.debug("get all available genres");
        return filmDao.getAllGenres();
    }

    public Genre getGenreById(int genreId) {
        log.debug("get genre with id = {}", genreId);
        return filmDao.getGenre(genreId)
                .orElseThrow(() -> new ObjectNotFoundException("Genre is not found"));
    }
}
