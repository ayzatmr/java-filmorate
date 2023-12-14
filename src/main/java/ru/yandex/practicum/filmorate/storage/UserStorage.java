package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAllUsers();

    User getUser(int userId);

    User addUser(User user);

    User updateUser(User user);

    User addFriend(User user);

    void deleteFriend(User user);

    List<User> getFriends(int userId);

    List<User> getCommonFriends(int userId, int friendId);

}
