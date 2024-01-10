package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.interfaces.GenreDao;
import ru.yandex.practicum.filmorate.model.Genre;

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
        return new LinkedHashSet<Genre>(jdbcTemplate.query(sqlQuery, BeanPropertyRowMapper.newInstance(Genre.class)));
    }
}
