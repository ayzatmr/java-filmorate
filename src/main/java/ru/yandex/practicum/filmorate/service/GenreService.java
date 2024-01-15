package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.interfaces.GenreDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedHashSet;

@Service
@Slf4j
public class GenreService {

    private final GenreDao genreDao;

    public GenreService(@Qualifier("GenreDaoImpl") GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    public LinkedHashSet<Genre> getAll() {
        log.debug("get all available genres");
        return genreDao.getAll();
    }

    public Genre get(int genreId) {
        log.debug("get genre with id = {}", genreId);
        return genreDao.get(genreId)
                .orElseThrow(() -> new ObjectNotFoundException("Genre is not found"));
    }
}
