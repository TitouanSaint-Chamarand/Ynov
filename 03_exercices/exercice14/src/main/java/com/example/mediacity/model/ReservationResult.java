package com.example.mediacity.model;

public sealed interface ReservationResult permits ReservationResult.Accepted, ReservationResult.Rejected {

    record Accepted(Reservation reservation) implements ReservationResult {
    }

    record Rejected(String reason) implements ReservationResult {
    }
}
