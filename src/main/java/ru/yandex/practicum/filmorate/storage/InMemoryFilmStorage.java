package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final Map<Integer, Film> films = new HashMap<>();
    private final AtomicInteger uniqueId = new AtomicInteger();
    private final UserStorage userStorage;

    @Autowired
    public InMemoryFilmStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    private User getUser(int userId) {
        return userStorage.findAllUsers().stream()
                .filter(u -> u.getId() == userId)
                .findAny()
                .orElse(null);
    }

    @Override
    public List<Film> findAllFilms() {
        return new ArrayList<>(films.values());
    }

    public Film getFilm(int filmId) {
        return films.get(filmId);
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(uniqueId.incrementAndGet());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else {
            return null;
        }
    }

    @Override
    public Film addLike(int filmId, int userId) {
        User user = getUser(userId);
        Film film = films.get(filmId);
        if (user != null && film != null) {
            film.setLikes(film.getLikes() + 1);
            film.addLikedUser(userId);
            return film;
        }
        return null;
    }

    @Override
    public Film deleteLike(int filmId, int userId) {
        User user = getUser(userId);
        Film film = films.get(filmId);
        if (user != null && film != null) {
            film.setLikes(film.getLikes() - 1);
            film.deleteLikedUser(userId);
            return film;
        }
        return null;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return films.values()
                .stream()
                .sorted(Comparator.comparing(Film::getLikes).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
