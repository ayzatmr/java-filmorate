package ru.yandex.practicum.filmorate.dao.interfaces;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.LinkedHashSet;
import java.util.Optional;

public interface RatingDao {

    Optional<Rating> getRating(int ratingId);

    LinkedHashSet<Rating> getAllRatings();
}
