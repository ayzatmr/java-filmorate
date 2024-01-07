package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@Jacksonized
public class User {
    private int id;

    @Email(message = "email should be valid")
    private String email;

    @NotEmpty(message = "login can not be empty")
    @Pattern(regexp = "^\\S+\\w{1,32}\\S+", message = "login should not contain spaces and special chars")
    private String login;

    private String name;

    @PastOrPresent(message = "birthday can not be in the future")
    @NotNull(message = "birthday can not be null")
    private LocalDate birthday;

    @JsonIgnore
    @Builder.Default
    private Set<Integer> friends = new HashSet<>();

    public Map<String, Object> toMap() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper.convertValue(this, new TypeReference<>() {
        });
    }

}
