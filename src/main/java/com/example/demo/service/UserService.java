package com.example.demo.service;

import com.example.demo.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service class for User operations.
 * Uses in-memory storage for demo purposes.
 */
@Service
public class UserService {

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    /**
     * Creates a new user.
     *
     * @param user the user to create
     * @return the created user with generated ID
     */
    public User createUser(User user) {
        Long id = idGenerator.incrementAndGet();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    /**
     * Gets a user by ID.
     *
     * @param id the user ID
     * @return Optional containing the user if found
     */
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    /**
     * Gets all users.
     *
     * @return list of all users
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    /**
     * Updates an existing user.
     *
     * @param id   the user ID
     * @param user the updated user data
     * @return Optional containing the updated user if found
     */
    public Optional<User> updateUser(Long id, User user) {
        if (users.containsKey(id)) {
            user.setId(id);
            users.put(id, user);
            return Optional.of(user);
        }
        return Optional.empty();
    }

    /**
     * Deletes a user by ID.
     *
     * @param id the user ID
     * @return true if the user was deleted, false otherwise
     */
    public boolean deleteUser(Long id) {
        return users.remove(id) != null;
    }

    /**
     * Clears all users (useful for testing).
     */
    public void clearAllUsers() {
        users.clear();
        idGenerator.set(0);
    }
}
