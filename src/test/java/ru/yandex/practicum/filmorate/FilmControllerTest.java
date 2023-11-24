package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTest {
    private final AtomicInteger uniqueId = new AtomicInteger();
    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private MockMvc mockMvc;

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private ObjectMapper objectMapper;

    @Test
    public void checkFindAllReturnsFilms() throws Exception {
        String contentAsString = this.mockMvc.perform(get("/films"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<Film> response = objectMapper.readValue(contentAsString, new TypeReference<List<Film>>() {
        });
        assertNotNull(response, "all films are returned");
    }

    @Test
    public void checkAddNewFilmPositive() throws Exception {
        Film film = Film.builder()
                .id(uniqueId.incrementAndGet())
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
    public void addNewFilmLombokValidationCheck() throws Exception {
        Film film = Film.builder()
                .id(uniqueId.incrementAndGet())
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
}
