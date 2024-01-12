package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.interfaces.FilmDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Qualifier("FilmDaoImpl")
public class FilmDaoImpl implements FilmDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> findAllFilms() {
        String sqlQuery = "select f.*, r.NAME as r_name from FILMS f join RATINGS R on f.RATING_ID = R.ID";
        List<Film> filmList = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
        return setFilmGenres(filmList);
    }

    public Optional<Film> getFilm(int filmId) {
        String sqlQuery = "select f.*, r.NAME from FILMS f join RATINGS R on f.RATING_ID = R.ID where f.id = ?;";
        try {
            Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, filmId);
            List<Film> films = setFilmGenres(Collections.singletonList(film));
            return Optional.ofNullable(films.get(0));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert insertFilm = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        int filmId = insertFilm.executeAndReturnKey(film.toInsertMap()).intValue();
        film.setId(filmId);
        return getFilm(filmId).orElse(null);
    }

    @Override
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
        List<Film> filmList = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
        return setFilmGenres(filmList);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Rating mpa = new Rating(resultSet.getInt("rating_id"), resultSet.getString(7));
        return Film.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getTimestamp("release_date").toLocalDateTime().toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(mpa)
                .build();
    }

    private List<Film> setFilmGenres(List<Film> films) {
        List<Integer> filmIds = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());

        String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        String sql = String.format("select fg.FILM_ID, g.* from GENRES g join FILM_GENRES fg on g.ID = fg.GENRE_ID where fg.FILM_ID in (%s)", inSql);

        List<FilmGenre> query = jdbcTemplate.query(
                sql,
                filmIds.toArray(),
                BeanPropertyRowMapper.newInstance(FilmGenre.class));

        Map<Integer, List<Genre>> mapGenres = new HashMap<>();
        query.forEach(filmGenre -> {
            if (mapGenres.get(filmGenre.getFilmId()) != null) {
                List<Genre> genres = new ArrayList<>(mapGenres.get(filmGenre.getFilmId()));
                genres.add(filmGenre.toGenre());
                mapGenres.put(filmGenre.getFilmId(), genres);
            } else {
                mapGenres.put(filmGenre.getFilmId(), Collections.singletonList(filmGenre.toGenre()));
            }
        });

        films.forEach(film -> film.setGenres(mapGenres.getOrDefault(film.getId(), new ArrayList<>())));
        return films;
    }
}

