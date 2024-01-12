package ru.yandex.practicum.filmorate.dao.inMemory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.interfaces.GenreDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.AbstractMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;

@Component
@Qualifier("InMemoryGenreDaoImpl")
public class InMemoryGenreDaoImpl implements GenreDao {

    private final Map<Integer, Genre> genres = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(1, new Genre(1, "Комедия")),
            new AbstractMap.SimpleEntry<>(2, new Genre(2, "Драма")),
            new AbstractMap.SimpleEntry<>(3, new Genre(3, "Мультфильм")),
            new AbstractMap.SimpleEntry<>(4, new Genre(4, "Триллер")),
            new AbstractMap.SimpleEntry<>(5, new Genre(5, "Документальный")),
            new AbstractMap.SimpleEntry<>(6, new Genre(6, "Боевик"))
    );

    public Optional<Genre> getGenre(int genreId) {
        return Optional.ofNullable(genres.get(genreId));
    }

    public LinkedHashSet<Genre> getAllGenres() {
        return new LinkedHashSet<>(genres.values());
    }

    @Override
    public Film addFilmGenre(Film film) {
        return film;
    }

    @Override
    public void deleteFilmGenre(Film film) {
    }
}
