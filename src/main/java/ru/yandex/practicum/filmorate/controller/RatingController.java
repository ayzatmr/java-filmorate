package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class RatingController {

    private final FilmService filmService;

    @GetMapping()
    public List<Rating> getAllRatings() {
        return filmService.getAllRatings();
    }

    @GetMapping("/{ratingId}")
    public Rating getRatingById(@PathVariable int ratingId) {
        return filmService.getRatingById(ratingId);
    }

}
