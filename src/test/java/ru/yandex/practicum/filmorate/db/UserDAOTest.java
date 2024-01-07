package ru.yandex.practicum.filmorate.db;

import lombok.RequiredArgsConstructor;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.dao.UserDaoImpl;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDAOTest {
    private final JdbcTemplate jdbcTemplate;

    @Qualifier("UserDaoImpl")
    private UserDao userDao;

    private User user;

    @BeforeAll
    public void beforeAll() {
        userDao = new UserDaoImpl(jdbcTemplate);
    }

    @BeforeEach
    public void beforeEach() {
        User newUser = User.builder()
                .name("Rayan Buc")
                .email("test@mail.ru")
                .birthday(LocalDate.of(1991, 11, 11))
                .login("test")
                .build();
        user = userDao.addUser(newUser);
    }

    @Test
    public void testFindUserById() {
        User savedUser = userDao.getUser(user.getId()).get();

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @Test
    public void testUpdateUser() {
        User newUser = User.builder()
                .id(user.getId())
                .name("New Name")
                .email("test2@mail.ru")
                .birthday(LocalDate.of(1993, 11, 11))
                .login("test2")
                .build();

        newUser = userDao.updateUser(newUser).get();
        User userById = userDao.getUser(newUser.getId()).get();
        assertThat(userById)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }

    @Test
    public void testFindAllUser() {
        List<User> allUsers = userDao.findAllUsers();
        MatcherAssert.assertThat(allUsers, Matchers.hasItems(
                hasProperty("name", is(user.getName())),
                hasProperty("login", is(user.getLogin()))
        ));
    }

    @Test
    public void testAddFriendAndCheckList() {
        User friend = User.builder()
                .name("friend")
                .email("friend@mail.ru")
                .birthday(LocalDate.of(1991, 11, 11))
                .login("test2")
                .build();
        friend = userDao.addUser(friend);

        userDao.addFriend(user.getId(), friend.getId());
        List<User> firstUserFriends = userDao.getFriends(user.getId()).get();
        List<User> secondUserFriends = userDao.getFriends(friend.getId()).get();
        MatcherAssert.assertThat(firstUserFriends, Matchers.hasItems(
                hasProperty("name", is(friend.getName())),
                hasProperty("id", is(friend.getId()))
        ));
        assertEquals(0, secondUserFriends.size());
    }

    @Test
    public void testAddSameFriendTwice() {
        User friend = User.builder()
                .name("friend")
                .email("friend@mail.ru")
                .birthday(LocalDate.of(1991, 11, 11))
                .login("test2")
                .build();
        friend = userDao.addUser(friend);

        userDao.addFriend(user.getId(), friend.getId());
        userDao.addFriend(user.getId(), friend.getId());
        List<User> friends = userDao.getFriends(user.getId()).get();
        assertEquals(1, friends.size());
    }

    @Test
    public void testDeleteFriend() {
        User friend = User.builder()
                .name("friend")
                .email("friend@mail.ru")
                .birthday(LocalDate.of(1991, 11, 11))
                .login("test2")
                .build();
        friend = userDao.addUser(friend);

        userDao.addFriend(user.getId(), friend.getId());
        userDao.deleteFriend(user.getId(), friend.getId());
        Optional<List<User>> friends = userDao.getFriends(user.getId());
        assertEquals(new ArrayList<>(), friends.get());
    }

    @Test
    public void testGetCommonFriends() {
        User newUser = User.builder()
                .name("user2")
                .email("user2@mail.ru")
                .birthday(LocalDate.of(1991, 11, 11))
                .login("test3")
                .build();
        newUser = userDao.addUser(newUser);

        User friend = User.builder()
                .name("friend")
                .email("friend@mail.ru")
                .birthday(LocalDate.of(1991, 11, 11))
                .login("test2")
                .build();
        friend = userDao.addUser(friend);

        userDao.addFriend(user.getId(), friend.getId());
        userDao.addFriend(newUser.getId(), friend.getId());

        List<User> commonFriends = userDao.getCommonFriends(user.getId(), newUser.getId()).get();
        MatcherAssert.assertThat(commonFriends, Matchers.hasItems(
                hasProperty("name", is(friend.getName())),
                hasProperty("id", is(friend.getId()))
        ));
    }
}