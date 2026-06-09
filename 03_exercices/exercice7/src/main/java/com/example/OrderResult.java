package com.example;

public sealed interface OrderResult permits OrderResult.Accepted, OrderResult.Rejected {

    record Accepted(OrderReceipt receipt) implements OrderResult {
    }

    record Rejected(String reason) implements OrderResult {
    }
}
