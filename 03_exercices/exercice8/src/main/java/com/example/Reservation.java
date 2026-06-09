package com.example;

import java.time.LocalDateTime;

public record Reservation(
        String userEmail,
        String roomCode,
        int participants,
        LocalDateTime start,
        LocalDateTime end
) {
}
