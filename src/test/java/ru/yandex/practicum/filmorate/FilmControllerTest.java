package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static org.hamcrest.Matchers.*;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FilmControllerTest {
    private final AtomicInteger uniqueId = new AtomicInteger();
    private User user;
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserStorage userStorage;

    @BeforeEach
    public void beforeEach() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    private Set<Integer> getRandomSet(int limit) {
        return new Random().ints(1, 101)
                .distinct()
                .limit(limit)
                .boxed()
                .collect(Collectors.toSet());
    }

    @BeforeAll
    public void setup() {
        user = User.builder()
                .name("Rayan")
                .email("test@mail.ru")
                .birthday(LocalDate.of(1991, 11, 11))
                .login("test")
                .build();
        userStorage.addUser(user);
    }


    @Test
    public void getAllFilmsCheck() throws Exception {
        Film film = Film.builder()
                .name("James Bond")
                .description("Good film")
                .duration(2)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .build();
        String json = objectMapper.writeValueAsString(film);

        this.mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        this.mockMvc.perform(get("/films"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("James Bond")));
    }

    @Test
    public void checkAddNewFilmPositive() throws Exception {
        Film film = Film.builder()
                .name("Titanik")
                .description("Best film")
                .duration(2)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .build();
        String json = objectMapper.writeValueAsString(film);

        String contentAsString = this.mockMvc
                .perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Film response = objectMapper.readValue(contentAsString, Film.class);
        assertNotNull(response, "film is created");
        assertEquals(film.getName(), response.getName(), "name is correct");
    }

    @Test
    public void addNewFilmCustomValidationCheck() throws Exception {
        Film film = Film.builder()
                .name("Titanik")
                .description("Best film")
                .duration(2)
                .releaseDate(LocalDate.of(1800, 1, 1))
                .build();
        String json = objectMapper.writeValueAsString(film);

        this.mockMvc
                .perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("date can not be more than")));
    }

    @Test
    public void addNewFilmLombokValidationCheck() throws Exception {
        Film film = Film.builder()
                .name("")
                .description(RandomStringUtils.randomAlphanumeric(201))
                .duration(-2)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .build();
        String json = objectMapper.writeValueAsString(film);

        this.mockMvc
                .perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("max description size is 200")))
                .andExpect(content().string(containsString("select positive duration in seconds only")))
                .andExpect(content().string(containsString("name can not be empty or null")));
    }

    @Test
    public void updateFilmLombokValidationCheck() throws Exception {
        Film film = Film.builder()
                .id(uniqueId.incrementAndGet())
                .name("")
                .description(RandomStringUtils.randomAlphanumeric(201))
                .duration(-2)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .build();
        String json = objectMapper.writeValueAsString(film);

        this.mockMvc
                .perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("max description size is 200")))
                .andExpect(content().string(containsString("select positive duration in seconds only")))
                .andExpect(content().string(containsString("name can not be empty or null")));
    }

    @Test
    public void updateFilmCustomValidationCheck() throws Exception {
        Film film = Film.builder()
                .id(uniqueId.incrementAndGet())
                .name("Titanik")
                .description("Best film")
                .duration(2)
                .releaseDate(LocalDate.of(1800, 1, 1))
                .build();
        String json = objectMapper.writeValueAsString(film);

        this.mockMvc
                .perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("date can not be more than")));
    }

    @Test
    public void updateFilmNotFoundException() throws Exception {
        Film film = Film.builder()
                .id(9999)
                .name("Titanik")
                .description("Best film")
                .duration(2)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .build();
        String json = objectMapper.writeValueAsString(film);

        this.mockMvc
                .perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Film is not found")));
    }

    @Test
    public void getFilmById() throws Exception {
        Film film = Film.builder()
                .name("James Bond")
                .description("Good film")
                .duration(2)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .build();
        String json = objectMapper.writeValueAsString(film);

        String contentAsString = this.mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        film = objectMapper.readValue(contentAsString, Film.class);

        this.mockMvc.perform(get("/films/{filmId}", film.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("James Bond")));
    }

    @Test
    public void getFilmByWrongId() throws Exception {
        this.mockMvc.perform(get("/films/{filmId}", 9999))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Film is not found")));
    }

    @Test
    public void getFilmByStringValue() throws Exception {
        this.mockMvc.perform(get("/films/{filmId}", "string"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Failed to convert value")));
    }

    @Test
    public void addLikeToFilmPositive() throws Exception {
        Film film = Film.builder()
                .name("James Bond")
                .description("Good film")
                .duration(2)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .build();
        String json = objectMapper.writeValueAsString(film);

        String createdFilm = this.mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        film = objectMapper.readValue(createdFilm, Film.class);

        this.mockMvc.perform(put("/films/{filmId}/like/{userId}", film.getId(), user.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes", is(1)));
    }

    @Test
    public void addLikeToNotExistingFilm() throws Exception {
        this.mockMvc.perform(put("/films/{filmId}/like/{userId}", 999, user.getId()))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Wrong film or userId is provided")));
    }

    @Test
    public void deleteLikeFromFilmPositive() throws Exception {
        Film film = Film.builder()
                .name("James Bond")
                .description("Good film")
                .duration(2)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .likedUsers(Collections.singleton(1))
                .build();
        String json = objectMapper.writeValueAsString(film);

        String createdFilm = this.mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        film = objectMapper.readValue(createdFilm, Film.class);

        this.mockMvc.perform(delete("/films/{filmId}/like/{userId}", film.getId(), user.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());

        String content = this.mockMvc.perform(get("/films/{filmId}", film.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("James Bond")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Film updatedFilm = objectMapper.readValue(content, Film.class);
        assertEquals(0, updatedFilm.getLikes(), "amount of likes is correct");
        assertEquals(Collections.emptySet(), updatedFilm.getLikedUsers(), "user is deleted from liked list");
    }

    @Test
    public void getPopularFilmsPositive() throws Exception {
        LinkedList<Film> films = new LinkedList<>();

        for (int i = 1; i < 4; i++) {
            Film film = Film.builder()
                    .name("James Bond")
                    .description("Good film")
                    .duration(2)
                    .releaseDate(LocalDate.of(2000, 1, 1))
                    .likedUsers(getRandomSet(i))
                    .build();
            String json = objectMapper.writeValueAsString(film);

            String createdFilm = this.mockMvc.perform(post("/films")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            film = objectMapper.readValue(createdFilm, Film.class);
            films.addFirst(film);
        }

        String content = this.mockMvc.perform(get("/films/popular"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("James Bond")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<Film> sortedFilms = objectMapper.readValue(content, new TypeReference<>() {
        });

        List<Integer> likes = sortedFilms.stream()
                .map(Film::getLikes)
                .collect(Collectors.toList());

        boolean isSorted = IntStream.range(1, likes.size())
                .map(index -> likes.get(index - 1).compareTo(likes.get(index)))
                .allMatch(order -> order >= 0);
        assertTrue(isSorted, "sorted correctly");
    }

    @Test
    public void getPopularFilmsWithZeroLikes() throws Exception {
        LinkedList<Film> films = new LinkedList<>();

        for (int i = 1; i < 4; i++) {
            Film film = Film.builder()
                    .id(uniqueId.incrementAndGet())
                    .name("James Bond")
                    .description("Good film")
                    .duration(2)
                    .releaseDate(LocalDate.of(2000, 1, 1))
                    .build();
            String json = objectMapper.writeValueAsString(film);

            String createdFilm = this.mockMvc.perform(post("/films")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            film = objectMapper.readValue(createdFilm, Film.class);
            films.add(film);
        }

        String content = this.mockMvc.perform(get("/films/popular"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("James Bond")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // sorted as added to map
        List<Film> sortedFilms = objectMapper.readValue(content, new TypeReference<>() {
        });
        assertEquals(films.getFirst(), sortedFilms.get(0), "first film is correct");
        assertEquals(films.getLast(), sortedFilms.get(sortedFilms.size() - 1), "last film is correct");
    }
}
