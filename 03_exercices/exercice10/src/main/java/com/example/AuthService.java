package com.example;

import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthResult register(String email, String username, String password) {
        if (userRepository.existsByUsername(username)) {
            return new AuthResult.RegistrationFailure("Username already exists");
        }

        userRepository.save(new User(email, username, password));
        return new AuthResult.RegistrationSuccess("Account created successfully");
    }

    public AuthResult login(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty() || !userOptional.get().password().equals(password)) {
            return new AuthResult.LoginFailure("Invalid credentials");
        }

        return new AuthResult.LoginSuccess("home");
    }
}
