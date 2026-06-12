package com.example.supportticket.service;

import com.example.supportticket.exception.InvalidStatusTransitionException;
import com.example.supportticket.exception.TicketNotFoundException;
import com.example.supportticket.model.Priority;
import com.example.supportticket.model.SupportTicket;
import com.example.supportticket.model.TicketStatus;
import com.example.supportticket.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupportTicketService {

    private final TicketRepository repository;

    public SupportTicketService(TicketRepository repository) {
        this.repository = repository;
    }

    public SupportTicket create(String title, Priority priority) {
        if (title == null || title.trim().length() < 3) {
            throw new IllegalArgumentException("Le titre doit contenir au moins 3 caractères");
        }
        if (priority == null) {
            throw new IllegalArgumentException("La priorité est obligatoire");
        }

        return repository.save(title.trim(), priority, TicketStatus.OPEN);
    }

    public SupportTicket getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
    }

    public List<SupportTicket> findAll() {
        return repository.findAll();
    }

    public SupportTicket updateStatus(Long id, TicketStatus newStatus) {
        SupportTicket ticket = getById(id);

        if (!isTransitionAllowed(ticket.status(), newStatus)) {
            throw new InvalidStatusTransitionException(ticket.status(), newStatus);
        }

        return repository.updateStatus(id, newStatus);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    private boolean isTransitionAllowed(TicketStatus current, TicketStatus target) {
        if (current == TicketStatus.RESOLVED) {
            return false;
        }

        return switch (current) {
            case OPEN -> target == TicketStatus.IN_PROGRESS || target == TicketStatus.RESOLVED;
            case IN_PROGRESS -> target == TicketStatus.RESOLVED;
            case RESOLVED -> false;
        };
    }
}
