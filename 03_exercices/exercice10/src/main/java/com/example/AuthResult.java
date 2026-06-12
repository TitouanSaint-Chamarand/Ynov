package com.example;

public sealed interface AuthResult {
    record RegistrationSuccess(String message) implements AuthResult {
    }

    record RegistrationFailure(String error) implements AuthResult {
    }

    record LoginSuccess(String redirectPage) implements AuthResult {
    }

    record LoginFailure(String errorMessage) implements AuthResult {
    }
}
