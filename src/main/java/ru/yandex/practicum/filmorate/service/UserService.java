package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    private void checkUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    public User getUser(int userId) {
        return Optional.ofNullable(userStorage.getUser(userId))
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));
    }

    public User addUser(User user) {
        checkUserName(user);
        log.debug("add new user: {}", user);
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        checkUserName(user);
        log.debug("update user: {}", user);
        return Optional.ofNullable(userStorage.updateUser(user))
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));
    }

    public User addFriend(int userId, int friendId) {
        log.debug("add friend with id = {} to user with id = {}", friendId, userId);
        return Optional.ofNullable(userStorage.addFriend(userId, friendId))
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));
    }

    public void deleteFriend(int userId, int friendId) {
        userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getFriends(int userId) {
        return Optional.ofNullable(userStorage.getFriends(userId))
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        return Optional.ofNullable(userStorage.getCommonFriends(userId, otherId))
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));
    }
}
