package com.example;

public sealed interface CheckoutResult {
    record Success(String confirmationMessage) implements CheckoutResult {
    }

    record Failure(String error) implements CheckoutResult {
    }
}
