package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
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
public class UserControllerTest {
    private final AtomicInteger uniqueId = new AtomicInteger();

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private MockMvc mockMvc;

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private ObjectMapper objectMapper;

    @Test
    public void checkGetAllUsers() throws Exception {
        User user = User.builder()
                .id(uniqueId.incrementAndGet())
                .name("Rayan Buc")
                .email("test@mail.ru")
                .birthday(LocalDate.of(1991, 11, 11))
                .login("test")
                .build();
        String json = objectMapper.writeValueAsString(user);

        this.mockMvc
                .perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json));

        this.mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Rayan Buc")));
    }

    @Test
    public void checkAddNewUserPositive() throws Exception {
        User user = User.builder()
                .id(uniqueId.incrementAndGet())
                .name("Rayan")
                .email("test@mail.ru")
                .birthday(LocalDate.of(1991, 11, 11))
                .login("test")
                .build();
        String json = objectMapper.writeValueAsString(user);

        String contentAsString = this.mockMvc
                .perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        User response = objectMapper.readValue(contentAsString, User.class);
        assertNotNull(response, "user is created");
        assertEquals(user.getName(), response.getName(), "name is correct");
    }

    @Test
    public void createUserWithEmptyNameShouldBeValid() throws Exception {
        User user = User.builder()
                .id(uniqueId.incrementAndGet())
                .email("test@mail.ru")
                .birthday(LocalDate.of(1991, 11, 11))
                .login("test")
                .build();
        String json = objectMapper.writeValueAsString(user);

        String contentAsString = this.mockMvc
                .perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        User response = objectMapper.readValue(contentAsString, User.class);
        assertNotNull(response, "user is created");
        assertEquals(user.getLogin(), response.getName(), "name is equal to login");
    }

    @Test
    public void addNewUserLombokValidationCheck() throws Exception {
        User user = User.builder()
                .id(uniqueId.incrementAndGet())
                .name("")
                .email("xxx")
                .birthday(LocalDate.now().plusDays(10))
                .login("test test")
                .build();
        String json = objectMapper.writeValueAsString(user);

        this.mockMvc
                .perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("email should be valid")))
                .andExpect(content().string(containsString("login should not contain spaces and special chars")))
                .andExpect(content().string(containsString("birthday can not be in the future")));
    }

    @Test
    public void updateUserPositive() throws Exception {
        User user = User.builder()
                .id(uniqueId.incrementAndGet())
                .name("Rayan")
                .email("test@mail.ru")
                .birthday(LocalDate.of(1991, 11, 11))
                .login("test")
                .build();
        String json = objectMapper.writeValueAsString(user);

        String contentAsString = this.mockMvc
                .perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        User response = objectMapper.readValue(contentAsString, User.class);
        assertNotNull(response, "user is created");
        assertEquals(user.getName(), response.getName(), "name is correct");
    }

    @Test
    public void updateUserLombokValidationCheck() throws Exception {
        User user = User.builder()
                .id(uniqueId.incrementAndGet())
                .name("")
                .email("xxx")
                .birthday(LocalDate.now().plusDays(10))
                .login("test test")
                .build();
        String json = objectMapper.writeValueAsString(user);

        this.mockMvc
                .perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("email should be valid")))
                .andExpect(content().string(containsString("login should not contain spaces and special chars")))
                .andExpect(content().string(containsString("birthday can not be in the future")));
    }

    @Test
    public void updateUserNotFoundException() throws Exception {
        User user = User.builder()
                .id(99999)
                .name("Rayan")
                .email("test@mail.ru")
                .birthday(LocalDate.of(1991, 11, 11))
                .login("test")
                .build();
        String json = objectMapper.writeValueAsString(user);

        this.mockMvc
                .perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("User not found")));
    }
}

