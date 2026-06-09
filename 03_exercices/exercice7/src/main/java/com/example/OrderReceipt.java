package com.example;

public record OrderReceipt(
        String productReference,
        int quantity,
        double totalAmount,
        String confirmationMessage
) {
}
