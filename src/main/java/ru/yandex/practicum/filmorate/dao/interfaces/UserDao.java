package ru.yandex.practicum.filmorate.dao.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    List<User> getAll();

    Optional<User> get(int userId);

    User add(User user);

    Optional<User> update(User user);

    Optional<User> addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    Optional<List<User>> getFriends(int userId);

    Optional<List<User>> getCommonFriends(int userId, int otherId);

}
