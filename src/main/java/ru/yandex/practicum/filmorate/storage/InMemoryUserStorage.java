package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static final Map<Integer, User> users = new HashMap<>();
    private final AtomicInteger uniqueId = new AtomicInteger();

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(int userId) {
        return users.get(userId);
    }

    @Override
    public User addUser(User user) {
        user.setId(uniqueId.incrementAndGet());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        } else {
            return null;
        }
    }

    @Override
    public User addFriend(User user) {

        return null;
    }

    @Override
    public void deleteFriend(User user) {

    }

    @Override
    public List<User> getFriends(int userId) {
        return null;
    }

    @Override
    public List<User> getCommonFriends(int userId, int friendId) {
        return null;
    }
}
