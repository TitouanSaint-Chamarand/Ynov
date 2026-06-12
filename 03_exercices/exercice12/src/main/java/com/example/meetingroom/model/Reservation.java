package com.example.meetingroom.model;

import java.time.LocalDateTime;

// Représente une réservation de salle
public record Reservation(
        Long id,
        Long roomId,
        String reserverName,
        LocalDateTime startTime,
        LocalDateTime endTime,
        ReservationStatus status
) {
}
