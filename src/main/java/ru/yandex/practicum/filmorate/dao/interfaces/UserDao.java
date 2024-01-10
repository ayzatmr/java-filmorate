package ru.yandex.practicum.filmorate.dao.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    List<User> findAllUsers();

    Optional<User> getUser(int userId);

    User addUser(User user);

    Optional<User> updateUser(User user);

    Optional<User> addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    Optional<List<User>> getFriends(int userId);

    Optional<List<User>> getCommonFriends(int userId, int otherId);

}
