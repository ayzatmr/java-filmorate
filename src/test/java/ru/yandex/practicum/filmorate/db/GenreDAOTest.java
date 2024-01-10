package ru.yandex.practicum.filmorate.db;

import lombok.RequiredArgsConstructor;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.GenreDaoImpl;
import ru.yandex.practicum.filmorate.dao.interfaces.GenreDao;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedHashSet;

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

    @BeforeAll
    public void beforeAll() {
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
        MatcherAssert.assertThat(genres, Matchers.hasItems(
                hasProperty("id", equalTo(1)),
                hasProperty("name", equalTo("Комедия"))
        ));
    }
}