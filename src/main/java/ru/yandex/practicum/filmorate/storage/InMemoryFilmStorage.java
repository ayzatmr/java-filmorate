package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final Map<Integer, Film> films = new HashMap<>();

    private final AtomicInteger uniqueId = new AtomicInteger();

    @Override
    public List<Film> findAllFilms() {
        return new ArrayList<>(films.values());
    }

    public Film getFilm(int filmId){
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
    public Film addLike(Film film) {
        return null;
    }

    @Override
    public void deleteLike(Film film) {

    }

    @Override
    public List<Film> getLikedFilms(int count) {
        return null;
    }
}
