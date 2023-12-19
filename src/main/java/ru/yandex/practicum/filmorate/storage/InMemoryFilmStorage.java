package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private final AtomicInteger uniqueId = new AtomicInteger();

    @Override
    public List<Film> findAllFilms() {
        return new ArrayList<>(films.values());
    }

    public Optional<Film> getFilm(int filmId) {
        return Optional.ofNullable(films.get(filmId));
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(uniqueId.incrementAndGet());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        Optional<Film> currentFilm = getFilm(film.getId());
        if (currentFilm.isPresent()) {
            films.put(film.getId(), film);
            return Optional.of(film);
        }
        return currentFilm;
    }

    @Override
    public Optional<Film> addLike(int filmId, int userId) {
        Optional<Film> film = getFilm(filmId);
        film.ifPresent(f -> f.getLikedUsers().add(userId));
        return film;
    }

    @Override
    public Optional<Film> deleteLike(int filmId, int userId) {
        Optional<Film> film = getFilm(filmId);
        film.ifPresent(f -> f.getLikedUsers().remove(userId));
        return film;
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
