package ru.yandex.practicum.filmorate.dao.interfaces;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.LinkedHashSet;
import java.util.Optional;

public interface RatingDao {

    Optional<Rating> get(int ratingId);

    LinkedHashSet<Rating> getAll();
}
