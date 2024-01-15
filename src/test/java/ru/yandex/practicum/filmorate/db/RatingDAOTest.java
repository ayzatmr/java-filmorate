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
import ru.yandex.practicum.filmorate.dao.RatingDaoImpl;
import ru.yandex.practicum.filmorate.dao.interfaces.RatingDao;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.LinkedHashSet;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RatingDAOTest {
    private final JdbcTemplate jdbcTemplate;

    @Qualifier("GenreDaoImpl")
    private RatingDao ratingDao;

    @BeforeAll
    public void beforeAll() {
        ratingDao = new RatingDaoImpl(jdbcTemplate);
    }

    @Test
    public void testGetRatingById() {
        Rating rating = ratingDao.get(1).get();
        assertEquals(1, rating.getId());
        assertEquals("G", rating.getName());
    }

    @Test
    public void testGetAllRatings() {
        LinkedHashSet<Rating> ratings = ratingDao.getAll();
        MatcherAssert.assertThat(ratings, Matchers.hasItems(
                hasProperty("id", equalTo(1)),
                hasProperty("name", equalTo("G"))
        ));
    }
}