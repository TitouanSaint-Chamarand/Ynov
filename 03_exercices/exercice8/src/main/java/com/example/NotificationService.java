package com.example;

import java.time.LocalDateTime;

public interface NotificationService {
    void sendConfirmation(String userEmail, String roomCode, LocalDateTime start, LocalDateTime end);
}
