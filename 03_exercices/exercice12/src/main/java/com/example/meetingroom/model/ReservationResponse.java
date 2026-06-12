package com.example.meetingroom.model;

import java.time.LocalDateTime;

public record ReservationResponse(
        Long id,
        Long roomId,
        String reserverName,
        LocalDateTime startTime,
        LocalDateTime endTime,
        ReservationStatus status
) {

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.id(),
                reservation.roomId(),
                reservation.reserverName(),
                reservation.startTime(),
                reservation.endTime(),
                reservation.status()
        );
    }
}
