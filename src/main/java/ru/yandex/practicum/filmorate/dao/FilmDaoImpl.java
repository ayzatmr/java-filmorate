package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Qualifier("FilmDaoImpl")
public class FilmDaoImpl implements FilmDao {
    private final JdbcTemplate jdbcTemplate;

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        List<Genre> genres = getFilmGenres(resultSet.getInt("id"));
        Rating mpa = new Rating(resultSet.getInt("rating_id"), resultSet.getString(7));

        return Film.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getTimestamp("release_date").toLocalDateTime().toLocalDate())
                .duration(resultSet.getInt("duration"))
                .genres(genres)
                .mpa(mpa)
                .build();
    }

    private List<Genre> getFilmGenres(int filmId) {
        String sqlQuery = "select g.* from GENRES g join FILM_GENRES fg on g.ID = fg.GENRE_ID where fg.FILM_ID = ?;";
        return jdbcTemplate.query(sqlQuery, BeanPropertyRowMapper.newInstance(Genre.class), filmId);
    }

    @Override
    public List<Film> findAllFilms() {
        String sqlQuery = "select f.*, r.NAME as r_name from FILMS f join RATINGS R on f.RATING_ID = R.ID";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    public Optional<Film> getFilm(int filmId) {
        String sqlQuery = "select f.*, r.NAME from FILMS f join RATINGS R on f.RATING_ID = R.ID where f.id = ?;";
        try {
            Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, filmId);
            return Optional.ofNullable(film);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Genre> getGenre(int genreId) {
        String sqlQuery = "select * from GENRES where id = ?;";
        try {
            Genre genre = jdbcTemplate.queryForObject(sqlQuery, BeanPropertyRowMapper.newInstance(Genre.class), genreId);
            return Optional.ofNullable(genre);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "select * from GENRES order by ID";
        return jdbcTemplate.query(sqlQuery, BeanPropertyRowMapper.newInstance(Genre.class));
    }

    @Override
    public Optional<Rating> getRating(int ratingId) {
        String sqlQuery = "select * from RATINGS where id = ?;";
        try {
            Rating rating = jdbcTemplate.queryForObject(sqlQuery, BeanPropertyRowMapper.newInstance(Rating.class), ratingId);
            return Optional.ofNullable(rating);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Rating> getAllRatings() {
        String sqlQuery = "select * from RATINGS order by ID";
        return jdbcTemplate.query(sqlQuery, BeanPropertyRowMapper.newInstance(Rating.class));
    }

    @Override
    @Transactional
    public Film addFilm(Film film) {
        SimpleJdbcInsert insertFilm = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        int filmId = insertFilm.executeAndReturnKey(film.toInsertMap()).intValue();
        film.setId(filmId);

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                String insertGenre = "insert into FILM_GENRES (film_id, genre_id) VALUES (?, ?);";
                jdbcTemplate.update(insertGenre,
                        filmId,
                        genre.getId());
            }
        }
        return getFilm(filmId).orElse(null);
    }

    @Override
    @Transactional
    public Optional<Film> updateFilm(Film film) {
        Optional<Film> currentFilm = getFilm(film.getId());
        if (currentFilm.isPresent()) {
            String sql = "update FILMS set NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATING_ID = ? where id = ?;";
            jdbcTemplate.update(sql,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());

            if (film.getGenres() != null) {
                String deleteSql = "delete from FILM_GENRES where film_id = ?";
                jdbcTemplate.update(deleteSql,
                        film.getId());
                for (Genre genre : film.getGenres()) {
                    String updateSql = "insert into FILM_GENRES (film_id, genre_id) VALUES (?, ?);";
                    jdbcTemplate.update(updateSql,
                            film.getId(),
                            genre.getId());
                }
            }
            return Optional.of(film);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Film> addLike(int filmId, int userId) {
        Optional<Film> film = getFilm(filmId);
        film.ifPresent(f -> {
            String sql = "insert into LIKES (user_id, film_id) select ?, ? WHERE NOT EXISTS ( SELECT user_id, film_id FROM LIKES WHERE user_id = ? and film_id = ? )";
            jdbcTemplate.update(sql,
                    userId,
                    filmId,
                    userId,
                    filmId);
            f.getLikedUsers().add(userId);
        });
        return film;
    }

    @Override
    public Optional<Film> deleteLike(int filmId, int userId) {
        Optional<Film> film = getFilm(filmId);
        film.ifPresent(f -> {
            String sql = "delete from LIKES where USER_ID = ? and FILM_ID = ?;";
            jdbcTemplate.update(sql,
                    userId,
                    filmId);
            f.getLikedUsers().remove(userId);

        });
        return film;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sqlQuery = "with q as (select count(*) as cnt, FILM_ID from LIKES group by FILM_ID) select f.*, r.NAME, q.cnt from FILMS f join RATINGS R on f.RATING_ID = R.ID left join q on f.ID = q.FILM_ID order by q.cnt desc limit ?;";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }
}

