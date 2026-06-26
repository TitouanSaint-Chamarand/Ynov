package com.example.mediacity.model;

import java.time.LocalDate;

public record Reservation(
        long id,
        String memberId,
        String workId,
        LocalDate createdAt,
        ReservationStatus status
) {

    public Reservation withStatus(ReservationStatus newStatus) {
        return new Reservation(id, memberId, workId, createdAt, newStatus);
    }
}
