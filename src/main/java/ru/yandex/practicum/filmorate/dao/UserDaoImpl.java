package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
@Qualifier("UserDaoImpl")
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .email(resultSet.getString("email"))
                .birthday(LocalDate.parse(resultSet.getString("birthday"), formatter))
                .login(resultSet.getString("login"))
                .build();
    }

    private Optional<User> getUserByLogin(String login) {
        String sqlQuery = "select * from USERS where login = ?;";
        try {
            User user = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, login);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAllUsers() {
        String sqlQuery = "select * from USERS;";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public Optional<User> getUser(int userId) {
        String sqlQuery = "select * from USERS where id = ?;";
        try {
            User user = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, userId);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public User addUser(User user) {
        Optional<User> userByLogin = getUserByLogin(user.getLogin());
        if (userByLogin.isPresent()) {
            throw new ValidationException("Login is reserved by another user");
        }
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        int userId = simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue();
        user.setId(userId);
        return user;
    }

    @Override
    public Optional<User> updateUser(User user) {
        Optional<User> userByLogin = getUserByLogin(user.getLogin());
        if (userByLogin.isPresent() && userByLogin.get().getId() != user.getId()) {
            throw new ValidationException("Login is reserved by another user");
        }
        String sqlQuery = "update users set email = ?, login = ?, name = ?, birthday = ? where id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday().format(formatter),
                user.getId());
        return Optional.of(user);
    }

    @Override
    public Optional<User> addFriend(int userId, int friendId) {
        Optional<User> currentUser = getUser(userId);
        Optional<User> friend = getUser(friendId);
        if (currentUser.isPresent() && friend.isPresent()) {
            SqlRowSet isFriends = jdbcTemplate.queryForRowSet(
                    "select * from friends where user_id = ? and friend_id = ?", userId, friendId);
            if (isFriends.next()) {
                return currentUser;
            }
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("user_id", userId);
            parameters.put("friend_id", friendId);
            new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("friends")
                    .execute(parameters);
            currentUser.get().getFriends().add(friendId);
            return currentUser;
        }
        return Optional.empty();
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        Optional<User> currentUser = getUser(userId);
        Optional<User> friend = getUser(friendId);
        if (currentUser.isPresent() && friend.isPresent()) {
            String sqlQuery = "delete from friends where user_id = ? and friend_id = ?";
            jdbcTemplate.update(sqlQuery, userId, friendId);
        }
    }

    @Override
    public Optional<List<User>> getFriends(int userId) {
        if (getUser(userId).isEmpty()) {
            return Optional.empty();
        }
        String sqlQuery = "select * from USERS where id in (select FRIEND_ID from FRIENDS where USER_ID = ?);";
        List<User> friends = jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId);
        return Optional.of(friends);
    }

    @Override
    public Optional<List<User>> getCommonFriends(int userId, int otherId) {
        Optional<User> currentUser = getUser(userId);
        Optional<User> friend = getUser(otherId);
        if (currentUser.isPresent() && friend.isPresent()) {
            String sqlQuery = "select * from USERS where id in (select FRIEND_ID from FRIENDS where USER_ID = ? and FRIEND_ID in (select FRIEND_ID from FRIENDS where USER_ID = ?));";
            List<User> friends = jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId, otherId);
            return Optional.of(friends);
        } else {
            return Optional.empty();
        }
    }
}
