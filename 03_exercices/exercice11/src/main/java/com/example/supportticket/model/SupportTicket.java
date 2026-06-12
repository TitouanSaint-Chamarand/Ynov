package com.example.supportticket.model;

public record SupportTicket(
        Long id,
        String title,
        Priority priority,
        TicketStatus status
) {
}
