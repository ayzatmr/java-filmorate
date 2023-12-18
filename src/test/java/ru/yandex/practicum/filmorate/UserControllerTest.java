package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
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
public class UserControllerTest {
    private final AtomicInteger uniqueId = new AtomicInteger();
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void beforeEach() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void checkGetAllUsers() throws Exception {
        User user = User.builder()
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
                .name("Rayan unique")
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
                .name("tommy update")
                .email("test" + uniqueId.incrementAndGet() + "@mail.ru")
                .birthday(LocalDate.of(1991, 11, 11))
                .login("test" + uniqueId.incrementAndGet())
                .build();
        String json = objectMapper.writeValueAsString(user);

        String createdUser = this.mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        user = objectMapper.readValue(createdUser, User.class);
        json = objectMapper.writeValueAsString(user);

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
        assertNotNull(response, "user is updated");
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

    @Test
    public void getUserById() throws Exception {
        User newUser = User.builder()
                .name("Billi Jones")
                .email("test" + uniqueId.incrementAndGet() + "@mail.ru")
                .birthday(LocalDate.of(1991, 11, 11))
                .login("test")
                .build();
        String json = objectMapper.writeValueAsString(newUser);

        String contentAsString = this.mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        User user = objectMapper.readValue(contentAsString, User.class);

        this.mockMvc.perform(get("/users/{userId}", user.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Billi Jones")));
    }

    @Test
    public void addNewFriend() throws Exception {
        LinkedList<User> users = new LinkedList<>();
        for (int i = 0; i < 2; i++) {
            User user = User.builder()
                    .name("tommy")
                    .email("test" + uniqueId.incrementAndGet() + "@mail.ru")
                    .birthday(LocalDate.of(1991, 11, 11))
                    .login("test" + uniqueId.get())
                    .build();
            String json = objectMapper.writeValueAsString(user);

            String createdUser = this.mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            user = objectMapper.readValue(createdUser, User.class);
            users.add(user);
        }

        this.mockMvc.perform(put("/users/{userId}/friends/{friendId}",
                users.getFirst().getId(), users.getLast().getId()))
                .andDo(print())
                .andExpect(status().isOk());

        String contentAsString = this.mockMvc.perform(get("/users/{userId}/friends", users.getFirst().getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<User> friends = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });

        assertEquals(users.getLast().getId(), friends.get(0).getId(), "user2 is friend of user1");
    }

    @Test
    public void deleteFriends() throws Exception {
        LinkedList<User> users = new LinkedList<>();
        for (int i = 0; i < 2; i++) {
            User user = User.builder()
                    .id(uniqueId.incrementAndGet())
                    .name("tommy")
                    .email("test" + uniqueId.get() + "@mail.ru")
                    .birthday(LocalDate.of(1991, 11, 11))
                    .login("test" + uniqueId.get())
                    .build();
            String json = objectMapper.writeValueAsString(user);

            String createdUser = this.mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            user = objectMapper.readValue(createdUser, User.class);
            users.add(user);
        }

        this.mockMvc.perform(put("/users/{userId}/friends/{friendId}",
                users.getFirst().getId(), users.getLast().getId()))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(delete("/users/{userId}/friends/{friendId}",
                users.getFirst().getId(), users.getLast().getId()))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String user1 = this.mockMvc.perform(get("/users/{userId}/friends", users.getFirst().getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<User> friends = objectMapper.readValue(user1, new TypeReference<>() {
        });

        assertEquals(Collections.emptyList(), friends, "friend is deleted for 1 user");

        String user2 = this.mockMvc.perform(get("/users/{userId}/friends", users.getFirst().getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<User> friends2 = objectMapper.readValue(user2, new TypeReference<>() {
        });
        assertEquals(Collections.emptyList(), friends2, "friend is deleted for 2 user");
    }

    @Test
    public void getFriends() throws Exception {
        LinkedList<User> users = new LinkedList<>();
        for (int i = 0; i < 2; i++) {
            User user = User.builder()
                    .name("tommy")
                    .email("test" + uniqueId.get() + "@mail.ru")
                    .birthday(LocalDate.of(1991, 11, 11))
                    .login("test" + uniqueId.get())
                    .build();
            String json = objectMapper.writeValueAsString(user);

            String createdUser = this.mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            user = objectMapper.readValue(createdUser, User.class);
            users.add(user);
        }

        this.mockMvc.perform(put("/users/{userId}/friends/{friendId}",
                users.getFirst().getId(), users.getLast().getId()))
                .andDo(print())
                .andExpect(status().isOk());

        String contentAsString = this.mockMvc.perform(get("/users/{userId}/friends", users.getFirst().getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<User> friends = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });

        assertEquals(users.getLast().getId(), friends.get(0).getId(), "user2 is friend of user1");
    }

    @Test
    public void getCommonFriends() throws Exception {
        LinkedList<User> users = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            User user = User.builder()
                    .id(uniqueId.incrementAndGet())
                    .name("tommy")
                    .email("test" + uniqueId.get() + "@mail.ru")
                    .birthday(LocalDate.of(1991, 11, 11))
                    .login("test" + uniqueId.get())
                    .build();
            String json = objectMapper.writeValueAsString(user);

            String createdUser = this.mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            user = objectMapper.readValue(createdUser, User.class);
            users.add(user);
        }

        this.mockMvc.perform(put("/users/{userId}/friends/{friendId}",
                users.getFirst().getId(), users.get(1).getId()))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(put("/users/{userId}/friends/{friendId}",
                users.getLast().getId(), users.get(1).getId()))
                .andDo(print())
                .andExpect(status().isOk());

        String contentAsString = this.mockMvc.perform(get("/users/{userId}/friends/common/{otherId}",
                users.getFirst().getId(), users.getLast().getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<User> commonFriends = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });

        assertEquals(users.get(1).getId(), commonFriends.get(0).getId(), "user1 and user3 have common friend user2");
    }
}

