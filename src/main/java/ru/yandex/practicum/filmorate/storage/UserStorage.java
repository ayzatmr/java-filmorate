package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAllUsers();

    User getUser(int userId);

    User addUser(User user);

    User updateUser(User user);

    User addFriend(int userId,  int friendId);

    void deleteFriend(int userId,  int friendId);

    List<User> getFriends(int userId);

    List<User> getCommonFriends(int userId, int otherId);

}
