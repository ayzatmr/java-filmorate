package ru.yandex.practicum.filmorate.dao.inMemory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.interfaces.UserDao;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@Qualifier("InMemoryUserDaoImpl")
public class InMemoryUserDaoImpl implements UserDao {
    private final Map<Integer, User> users = new HashMap<>();
    private final AtomicInteger uniqueId = new AtomicInteger();

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> get(int userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public User add(User user) {
        user.setId(uniqueId.incrementAndGet());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> update(User user) {
        Optional<User> currentUser = get(user.getId());
        if (currentUser.isPresent()) {
            users.put(user.getId(), user);
            return Optional.of(user);
        }
        return currentUser;
    }

    @Override
    public Optional<User> addFriend(int userId, int friendId) {
        Optional<User> currentUser = get(userId);
        Optional<User> friend = get(friendId);
        if (currentUser.isPresent() && friend.isPresent()) {
            currentUser.get().getFriends().add(friendId);
            friend.get().getFriends().add(userId);
            return currentUser;
        }
        return Optional.empty();
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        Optional<User> currentUser = get(userId);
        Optional<User> friend = get(friendId);
        if (currentUser.isPresent() && friend.isPresent()) {
            currentUser.get().getFriends().remove(friendId);
            friend.get().getFriends().remove(userId);
        }
    }

    @Override
    public Optional<List<User>> getFriends(int userId) {
        Optional<User> currentUser = get(userId);
        return currentUser.map(user -> user.getFriends()
                .stream()
                .map(users::get)
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<List<User>> getCommonFriends(int userId, int otherId) {
        Optional<User> currentUser = get(userId);
        Optional<User> friend = get(otherId);
        if (currentUser.isPresent() && friend.isPresent()) {
            return currentUser.map(user -> user.getFriends()
                    .stream()
                    .filter(friend.get().getFriends()::contains)
                    .map(users::get)
                    .collect(Collectors.toList()));
        } else {
            return Optional.empty();
        }
    }
}
