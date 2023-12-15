package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Builder
@Jacksonized
public class Film {
    private int id;

    @NotEmpty(message = "name can not be empty or null")
    private String name;

    @Size(max = 200, message = "max description size is 200")
    private String description;

    private LocalDate releaseDate;

    @Positive(message = "select positive duration in seconds only")
    private int duration; // in seconds

    private Set<Integer> likedUsers;

    private int likes;

    public Set<Integer> getLikedUsers() {
        return Objects.requireNonNullElseGet(likedUsers, HashSet::new);
    }

    public void addLikedUser(int userId) {
        Set<Integer> likedUsers = getLikedUsers();
        likedUsers.add(userId);
    }

    public void deleteLikedUser(int userId) {
        Set<Integer> likedUsers = getLikedUsers();
        likedUsers.remove(userId);
    }
}
