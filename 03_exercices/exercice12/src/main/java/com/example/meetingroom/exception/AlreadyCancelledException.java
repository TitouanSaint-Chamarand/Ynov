package com.example.meetingroom.exception;

public class AlreadyCancelledException extends RuntimeException {

    public AlreadyCancelledException(Long id) {
        super("La réservation " + id + " est déjà annulée");
    }
}
