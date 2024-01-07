package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

@Data
@Builder
@Jacksonized

public class Film {
    private int id;

    @NotEmpty(message = "name can not be empty or null")
    private String name;

    @Size(max = 200, message = "max description size is 200")
    @NotNull
    private String description;

    private LocalDate releaseDate;

    @Positive(message = "select positive duration in seconds only")
    private int duration; // in seconds

    @JsonIgnore
    @Builder.Default
    private Set<Integer> likedUsers = new HashSet<>();

    @Nullable
    private List<Genre> genres;

    @NotNull(message = "rating can not be null")
    private Rating mpa;

    public int getLikes() {
        return likedUsers.size();
    }

    public Map<String, Object> toInsertMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("description", description);
        map.put("release_date", releaseDate);
        map.put("duration", duration);
        map.put("rating_id", mpa.getId());
        return map;
    }
}
