package com.example.supportticket.repository;

import com.example.supportticket.model.Priority;
import com.example.supportticket.model.SupportTicket;
import com.example.supportticket.model.TicketStatus;

import java.util.List;
import java.util.Optional;

public interface TicketRepository {

    SupportTicket save(String title, Priority priority, TicketStatus status);

    Optional<SupportTicket> findById(Long id);

    List<SupportTicket> findAll();

    SupportTicket updateStatus(Long id, TicketStatus status);

    void deleteAll();
}
