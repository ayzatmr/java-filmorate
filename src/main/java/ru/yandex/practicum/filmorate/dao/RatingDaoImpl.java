package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.interfaces.RatingDao;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.LinkedHashSet;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Qualifier("RatingDaoImpl")
public class RatingDaoImpl implements RatingDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Rating> get(int ratingId) {
        String sqlQuery = "select * from RATINGS where id = ?;";
        try {
            Rating rating = jdbcTemplate.queryForObject(sqlQuery, BeanPropertyRowMapper.newInstance(Rating.class), ratingId);
            return Optional.of(rating);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public LinkedHashSet<Rating> getAll() {
        String sqlQuery = "select * from RATINGS order by ID";
        return new LinkedHashSet<>(jdbcTemplate.query(sqlQuery, BeanPropertyRowMapper.newInstance(Rating.class)));
    }
}
