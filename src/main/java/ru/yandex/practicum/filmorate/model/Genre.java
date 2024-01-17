package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Data
@Jacksonized
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Genre {
    @NotNull
    private int id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;
}
