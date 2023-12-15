package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Builder
@Jacksonized
public class User {
    private int id;

    @Email(message = "email should be valid")
    private String email;

    @NotEmpty(message = "login can not be empty")
    @Pattern(regexp = "^\\S+\\w{1,32}\\S{1,}", message = "login should not contain spaces and special chars")
    private String login;

    private String name;

    @Past(message = "birthday can not be in the future")
    private LocalDate birthday;

    private Set<Integer> friends;

    public Set<Integer> getFriends() {
        return Objects.requireNonNullElseGet(friends, HashSet::new);
    }

    public void addNewFriend(int userId) {
        Set<Integer> friends = getFriends();
        friends.add(userId);
        this.friends = friends;
    }

    public void deleteFriend(int userId) {
        Set<Integer> friends = getFriends();
        friends.remove(userId);
        this.friends = friends;
    }
}
