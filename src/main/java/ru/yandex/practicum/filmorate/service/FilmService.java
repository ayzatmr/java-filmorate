package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dao.interfaces.FilmDao;
import ru.yandex.practicum.filmorate.dao.interfaces.GenreDao;
import ru.yandex.practicum.filmorate.dao.interfaces.UserDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmDao filmDao;

    private final UserDao userDao;

    private final GenreDao genreDao;

    private static final LocalDate MAX_DATE = LocalDate.of(1895, 12, 12);

    public FilmService(@Qualifier("FilmDaoImpl") FilmDao filmDao, @Qualifier("UserDaoImpl") UserDao userDao, @Qualifier("GenreDaoImpl") GenreDao genreDao) {
        this.filmDao = filmDao;
        this.userDao = userDao;
        this.genreDao = genreDao;
    }

    public List<Film> getAll() {
        return filmDao.getAll();
    }

    public Film getFilm(int filmId) {
        return filmDao.get(filmId)
                .orElseThrow(() -> new ObjectNotFoundException("Film is not found"));
    }

    @Transactional
    public Film add(Film film) {
        checkFilmMaxDate(film);
        log.debug("add new film: {}", film);
        Film addedFilm = filmDao.add(film);
        film.setId(addedFilm.getId());
        return genreDao.addFilmGenre(film);
    }

    @Transactional
    public Film update(Film film) {
        checkFilmMaxDate(film);
        log.debug("update film: {}", film);
        filmDao.update(film)
                .orElseThrow(() -> new ObjectNotFoundException("Film is not found"));
        genreDao.deleteFilmGenre(film);
        return genreDao.addFilmGenre(film);
    }

    public Film addLike(int filmId, int userId) {
        log.debug("add like by userId = {} to film with id = {}", userId, filmId);
        if (userDao.get(userId).isEmpty()) {
            throw new ObjectNotFoundException("User not found");
        }
        return filmDao.addLike(filmId, userId)
                .orElseThrow(() -> new ObjectNotFoundException("Wrong film or userId is provided"));
    }

    public void deleteLike(int filmId, int userId) {
        log.debug("delete like by userId = {} from film with id = {}", userId, filmId);
        if (userDao.get(userId).isEmpty()) {
            throw new ObjectNotFoundException("User not found");
        }
        filmDao.deleteLike(filmId, userId)
                .orElseThrow(() -> new ObjectNotFoundException("Wrong film or userId is presented"));
    }

    public List<Film> getPopularFilms(int count) {
        log.debug("Get popular films with limit = {}", count);
        return filmDao.getPopularFilms(count);
    }

    private void checkFilmMaxDate(Film film) {
        if (film.getReleaseDate().isBefore(MAX_DATE)) {
            throw new ValidationException("date can not be more than " + MAX_DATE.toString());
        }
    }
}
