package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    public User getUser(int userId) {
        User user = userStorage.getUser(userId);
        if (user == null) {
            throw new ObjectNotFoundException("User not found");
        }
        return user;
    }

    public User addUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        log.debug("add new user: {}", user);
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        log.debug("update user: {}", user);
        User newUser = userStorage.updateUser(user);
        if (newUser == null) {
            throw new ObjectNotFoundException("User not found");
        }
        return user;
    }
}
