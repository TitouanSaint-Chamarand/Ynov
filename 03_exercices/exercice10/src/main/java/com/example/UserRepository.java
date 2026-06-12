package com.example;

import java.util.Optional;

public interface UserRepository {
    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    void save(User user);
}
