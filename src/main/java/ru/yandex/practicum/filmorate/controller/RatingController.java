package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @GetMapping()
    public List<Rating> getAllRatings() {
        return new ArrayList<>(ratingService.getAll());
    }

    @GetMapping("/{ratingId}")
    public Rating getRatingById(@PathVariable int ratingId) {
        return ratingService.get(ratingId);
    }

}
