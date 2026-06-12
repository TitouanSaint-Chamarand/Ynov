package com.example.supportticket.exception;

import com.example.supportticket.model.TicketStatus;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(TicketStatus currentStatus, TicketStatus targetStatus) {
        super("Transition de statut interdite : " + currentStatus + " vers " + targetStatus);
    }
}
