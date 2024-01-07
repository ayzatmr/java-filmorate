package ru.yandex.practicum.filmorate.db;

import lombok.RequiredArgsConstructor;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.FilmDaoImpl;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.dao.UserDaoImpl;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FilmDAOTest {
    private final JdbcTemplate jdbcTemplate;

    @Qualifier("FilmDaoImpl")
    private FilmDao filmDao;

    @Qualifier("UserDaoImpl")
    private UserDao userDao;

    private Film film;
    private User user;


    @BeforeAll
    public void beforeAll() {
        filmDao = new FilmDaoImpl(jdbcTemplate);
        userDao = new UserDaoImpl(jdbcTemplate);

        User newUser = User.builder()
                .name("Rayan Buc")
                .email("test@mail.ru")
                .birthday(LocalDate.of(1991, 11, 11))
                .login("test")
                .build();
        user = userDao.addUser(newUser);
    }

    @BeforeEach
    public void beforeEach() {
        Film newFilm = Film.builder()
                .name("James Bond")
                .description("Good film")
                .duration(2)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .mpa(new Rating(1, "G"))
                .genres(List.of(new Genre(1, "Комедия")))
                .build();
        film = filmDao.addFilm(newFilm);
    }

    @Test
    public void testFindFilmById() {
        Film savedFilm = filmDao.getFilm(film.getId()).get();

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    public void testGetAllFilms() {
        List<Film> allFilms = filmDao.findAllFilms();

        MatcherAssert.assertThat(allFilms, Matchers.hasItems(
                hasProperty("name", is(film.getName())),
                hasProperty("description", is(film.getDescription())),
                hasProperty("duration", is(film.getDuration())),
                hasProperty("releaseDate", is(film.getReleaseDate())),
                hasProperty("mpa", hasProperty("id", is(film.getMpa().getId()))),
                hasProperty("mpa", hasProperty("name", is(film.getMpa().getName()))),
                hasProperty("genres", is(film.getGenres()))
        ));
    }

    @Test
    public void testGetGenreById() {
        Genre genre = filmDao.getGenre(1).get();
        assertEquals(1, genre.getId());
        assertEquals("Комедия", genre.getName());
    }

    @Test
    public void testGetAllGenres() {
        List<Genre> genres = filmDao.getAllGenres();
        MatcherAssert.assertThat(genres, Matchers.hasItems(
                hasProperty("id", equalTo(1)),
                hasProperty("name", equalTo("Комедия"))
        ));
    }

    @Test
    public void testGetRatingById() {
        Rating rating = filmDao.getRating(1).get();
        assertEquals(1, rating.getId());
        assertEquals("G", rating.getName());
    }

    @Test
    public void testGetAllRatings() {
        List<Rating> ratings = filmDao.getAllRatings();
        MatcherAssert.assertThat(ratings, Matchers.hasItems(
                hasProperty("id", equalTo(1)),
                hasProperty("name", equalTo("G"))
        ));
    }

    @Test
    public void testUpdateFilm() {
        Film newFilm = Film.builder()
                .id(film.getId())
                .name("updated name")
                .description("updated desc")
                .duration(12)
                .releaseDate(LocalDate.of(1999, 1, 1))
                .mpa(new Rating(2, "PG"))
                .genres(List.of(new Genre(6, "Боевик")))
                .build();
        newFilm = filmDao.updateFilm(newFilm).get();

        Film filmById = filmDao.getFilm(newFilm.getId()).get();
        assertThat(filmById)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newFilm);
    }

    @Test
    public void testAddLikeToFilm() {
        Optional<Film> film = filmDao.addLike(this.film.getId(), user.getId());
        assertEquals(1, film.get().getLikes());
    }

    @Test
    public void testDeleteLikeFromFilm() {
        filmDao.addLike(this.film.getId(), user.getId());
        Optional<Film> film = filmDao.deleteLike(this.film.getId(), user.getId());
        assertEquals(0, film.get().getLikes());
    }

    @Test
    public void testGetPopularFilms() {
        Film newFilm = Film.builder()
                .name("James Bond 2")
                .description("Good film")
                .duration(2)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .mpa(new Rating(1, "G"))
                .genres(List.of(new Genre(1, "Комедия")))
                .build();
        newFilm = filmDao.addFilm(newFilm);

        User user2 = User.builder()
                .name("Rayan Buc")
                .email("test2@mail.ru")
                .birthday(LocalDate.of(1991, 11, 11))
                .login("test2")
                .build();
        user2 = userDao.addUser(user2);

        filmDao.addLike(film.getId(), user.getId());
        filmDao.addLike(film.getId(), user2.getId());
        filmDao.addLike(newFilm.getId(), user2.getId());
        List<Film> popularFilms = filmDao.getPopularFilms(3);

        assertEquals(2, popularFilms.size());
        assertEquals(film.getId(), popularFilms.get(0).getId());
        assertEquals(newFilm.getId(), popularFilms.get(1).getId());
    }
}