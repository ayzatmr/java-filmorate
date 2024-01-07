package ru.yandex.practicum.filmorate.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.InMemoryFilmDaoImpl;
import ru.yandex.practicum.filmorate.dao.InMemoryUserDaoImpl;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

@TestConfiguration
public class TestConfig {

    UserDao userDao = new InMemoryUserDaoImpl();
    FilmDao filmDao = new InMemoryFilmDaoImpl();

    @Bean
    @Primary
    public UserService testUserService() {
        return new UserService(userDao);
    }

    @Bean
    @Primary
    public FilmService testFilmService() {
        return new FilmService(filmDao, userDao);
    }
}