package com.example;

public sealed interface CartOperationResult {
    record Success(String message) implements CartOperationResult {
    }

    record Failure(String error) implements CartOperationResult {
    }
}
