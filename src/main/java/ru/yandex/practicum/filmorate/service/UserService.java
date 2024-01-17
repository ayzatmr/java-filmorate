package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.interfaces.UserDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Service
@Slf4j
public class UserService {

    private final UserDao userDao;

    public UserService(@Qualifier("UserDaoImpl") UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> getAll() {
        return userDao.getAll();
    }

    public User get(int userId) {
        return userDao.get(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));
    }

    public User add(User user) {
        checkUserName(user);
        log.debug("add new user: {}", user);
        return userDao.add(user);
    }

    public User update(User user) {
        checkUserName(user);
        log.debug("update user: {}", user);
        return userDao.update(user)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));
    }

    public User addFriend(int userId, int friendId) {
        log.debug("add friend with id = {} to user with id = {}", friendId, userId);
        if (userId == friendId) {
            throw new ValidationException("Provide different userId and friendId");
        }
        return userDao.addFriend(userId, friendId)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));
    }

    public void deleteFriend(int userId, int friendId) {
        userDao.deleteFriend(userId, friendId);
    }

    public List<User> getFriends(int userId) {
        return userDao.getFriends(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        if (userId == otherId) {
            throw new ValidationException("Provide different userId and friendId");
        }
        return userDao.getCommonFriends(userId, otherId)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));
    }

    private void checkUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
