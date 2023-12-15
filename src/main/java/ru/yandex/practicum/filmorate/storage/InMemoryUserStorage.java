package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
    public User addFriend(int userId, int friendId) {
        User currentUser = users.get(userId);
        User friend = users.get(friendId);
        if (currentUser != null && friend != null) {
            currentUser.getFriends().add(friendId);
            friend.addNewFriend(userId);
            return currentUser;
        }
        return null;
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        User currentUser = users.get(userId);
        User friend = users.get(friendId);
        if (currentUser != null && friend != null) {
            currentUser.deleteFriend(friendId);
            friend.deleteFriend(userId);
        }
    }

    @Override
    public List<User> getFriends(int userId) {
        User currentUser = users.get(userId);
        if (currentUser != null) {
            return currentUser.getFriends()
                    .stream()
                    .map(users::get)
                    .collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        User currentUser = users.get(userId);
        User friend = users.get(otherId);
        if (currentUser != null && friend != null) {
            return currentUser.getFriends()
                    .stream()
                    .filter(friend.getFriends()::contains)
                    .map(users::get)
                    .collect(Collectors.toList());
        }
        return null;
    }
}
