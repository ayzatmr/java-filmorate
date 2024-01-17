package ru.yandex.practicum.filmorate.dao.inMemory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.interfaces.FilmDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@Qualifier("InMemoryFilmDaoImpl")
public class InMemoryFilmDaoImpl implements FilmDao {
    private final Map<Integer, Film> films = new HashMap<>();
    private final AtomicInteger uniqueId = new AtomicInteger();

    private final Map<Integer, Rating> ratings = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(1, new Rating(1, "G")),
            new AbstractMap.SimpleEntry<>(2, new Rating(2, "PG")),
            new AbstractMap.SimpleEntry<>(3, new Rating(3, "PG-13")),
            new AbstractMap.SimpleEntry<>(4, new Rating(4, "R")),
            new AbstractMap.SimpleEntry<>(5, new Rating(5, "NC-17"))
    );

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> get(int filmId) {
        return Optional.ofNullable(films.get(filmId));
    }

    @Override
    public Film add(Film film) {
        film.setId(uniqueId.incrementAndGet());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Optional<Film> update(Film film) {
        Optional<Film> currentFilm = get(film.getId());
        if (currentFilm.isPresent()) {
            films.put(film.getId(), film);
            return Optional.of(film);
        }
        return currentFilm;
    }

    @Override
    public Optional<Film> addLike(int filmId, int userId) {
        Optional<Film> film = get(filmId);
        film.ifPresent(f -> f.getLikedUsers().add(userId));
        return film;
    }

    @Override
    public Optional<Film> deleteLike(int filmId, int userId) {
        Optional<Film> film = get(filmId);
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

    public Optional<Rating> getRating(int ratingId) {
        return Optional.ofNullable(ratings.get(ratingId));
    }

    public List<Rating> getAllRatings() {
        return new ArrayList<>(ratings.values());
    }
}
