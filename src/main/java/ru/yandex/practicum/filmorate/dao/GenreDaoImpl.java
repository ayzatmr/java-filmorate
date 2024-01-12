package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.interfaces.GenreDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Qualifier("GenreDaoImpl")
public class GenreDaoImpl implements GenreDao {
    private final JdbcTemplate jdbcTemplate;

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
    public LinkedHashSet<Genre> getAllGenres() {
        String sqlQuery = "select * from GENRES order by ID";
        return new LinkedHashSet<>(jdbcTemplate.query(sqlQuery, BeanPropertyRowMapper.newInstance(Genre.class)));
    }

    @Override
    public Film addFilmGenre(Film film) {
        if (film.getGenres() != null) {
            String sql = "insert into FILM_GENRES (film_id, genre_id) VALUES (?, ?);";
            jdbcTemplate.batchUpdate(sql,
                    new BatchPreparedStatementSetter() {
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setInt(1, film.getId());
                            ps.setInt(2, film.getGenres().get(i).getId());
                        }

                        public int getBatchSize() {
                            return film.getGenres().size();
                        }
                    });
        }
        return film;
    }

    @Override
    public void deleteFilmGenre(Film film) {
        String deleteSql = "delete from FILM_GENRES where film_id = ?";
        jdbcTemplate.update(deleteSql,
                film.getId());
    }
}
