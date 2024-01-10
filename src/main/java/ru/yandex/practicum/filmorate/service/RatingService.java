package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.interfaces.RatingDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.LinkedHashSet;

@Service
@Slf4j
public class RatingService {

    private final RatingDao ratingDao;

    public RatingService(@Qualifier("RatingDaoImpl") RatingDao ratingDao) {
        this.ratingDao = ratingDao;
    }

    public LinkedHashSet<Rating> getAllRatings() {
        log.debug("get all available ratings");
        return ratingDao.getAllRatings();
    }

    public Rating getRatingById(int ratingId) {
        log.debug("get rating with id = {}", ratingId);
        return ratingDao.getRating(ratingId)
                .orElseThrow(() -> new ObjectNotFoundException("Rating is not found"));
    }
}
