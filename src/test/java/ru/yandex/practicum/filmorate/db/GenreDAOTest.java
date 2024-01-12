package ru.yandex.practicum.filmorate.db;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.FilmDaoImpl;
import ru.yandex.practicum.filmorate.dao.GenreDaoImpl;
import ru.yandex.practicum.filmorate.dao.interfaces.FilmDao;
import ru.yandex.practicum.filmorate.dao.interfaces.GenreDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GenreDAOTest {
    private final JdbcTemplate jdbcTemplate;

    @Qualifier("GenreDaoImpl")
    private GenreDao genreDao;

    @Qualifier("FilmDaoImpl")
    private FilmDao filmDao;

    @BeforeAll
    public void beforeAll() {
        filmDao = new FilmDaoImpl(jdbcTemplate);
        genreDao = new GenreDaoImpl(jdbcTemplate);
    }

    @Test
    public void testGetGenreById() {
        Genre genre = genreDao.getGenre(1).get();
        assertEquals(1, genre.getId());
        assertEquals("Комедия", genre.getName());
    }

    @Test
    public void testGetAllGenres() {
        LinkedHashSet<Genre> genres = genreDao.getAllGenres();
        assertThat(genres, Matchers.hasItems(
                hasProperty("id", equalTo(1)),
                hasProperty("name", equalTo("Комедия"))
        ));
    }

    @Test
    public void createFilmWithGenre() {
        List<Genre> genres = List.of(new Genre(1, "Комедия"));
        Film newFilm = Film.builder()
                .name("James Bond")
                .description("Good film")
                .duration(2)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .mpa(new Rating(1, "G"))
                .build();
        Film createdFilm = filmDao.addFilm(newFilm);

        newFilm = newFilm.toBuilder()
                .id(createdFilm.getId())
                .genres(genres)
                .build();
        createdFilm = genreDao.addFilmGenre(newFilm);

        Assertions.assertThat(createdFilm.getGenres())
                .hasSize(1)
                .extracting(Genre::getName)
                .containsExactlyInAnyOrder("Комедия");
    }

    @Test
    public void createFilmWithTwoGenres() {
        List<Genre> genres = List.of(new Genre(1, "Комедия"), new Genre(2, "Драма"));
        Film newFilm = Film.builder()
                .name("James Bond")
                .description("Good film")
                .duration(2)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .mpa(new Rating(1, "G"))
                .build();
        Film createdFilm = filmDao.addFilm(newFilm);

        newFilm = newFilm.toBuilder()
                .id(createdFilm.getId())
                .genres(genres)
                .build();
        createdFilm = genreDao.addFilmGenre(newFilm);

        Assertions.assertThat(createdFilm.getGenres())
                .hasSize(2)
                .extracting(Genre::getName)
                .containsExactlyInAnyOrder("Комедия", "Драма");
    }

    @Test
    public void deleteFilmGenre() {
        List<Genre> genres = List.of(new Genre(1, "Комедия"));
        Film newFilm = Film.builder()
                .name("James Bond")
                .description("Good film")
                .duration(2)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .mpa(new Rating(1, "G"))
                .build();
        Film createdFilm = filmDao.addFilm(newFilm);

        newFilm = newFilm.toBuilder()
                .id(createdFilm.getId())
                .genres(genres)
                .build();
        createdFilm = genreDao.addFilmGenre(newFilm);
        genreDao.deleteFilmGenre(createdFilm);
        Film savedFilm = filmDao.getFilm(createdFilm.getId()).get();
        assertEquals(0, savedFilm.getGenres().size());
    }
}