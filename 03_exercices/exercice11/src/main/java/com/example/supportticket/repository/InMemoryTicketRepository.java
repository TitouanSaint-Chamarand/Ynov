package com.example.supportticket.repository;

import com.example.supportticket.model.Priority;
import com.example.supportticket.model.SupportTicket;
import com.example.supportticket.model.TicketStatus;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryTicketRepository implements TicketRepository {

    private final AtomicLong sequence = new AtomicLong(0);
    private final Map<Long, SupportTicket> tickets = new ConcurrentHashMap<>();

    @Override
    public SupportTicket save(String title, Priority priority, TicketStatus status) {
        Long id = sequence.incrementAndGet();
        SupportTicket ticket = new SupportTicket(id, title, priority, status);
        tickets.put(id, ticket);
        return ticket;
    }

    @Override
    public Optional<SupportTicket> findById(Long id) {
        return Optional.ofNullable(tickets.get(id));
    }

    @Override
    public List<SupportTicket> findAll() {
        return new ArrayList<>(tickets.values())
                .stream()
                .sorted(Comparator.comparing(SupportTicket::id))
                .toList();
    }

    @Override
    public SupportTicket updateStatus(Long id, TicketStatus status) {
        SupportTicket current = tickets.get(id);
        SupportTicket updated = new SupportTicket(
                current.id(),
                current.title(),
                current.priority(),
                status
        );
        tickets.put(id, updated);
        return updated;
    }

    @Override
    public void deleteAll() {
        tickets.clear();
        sequence.set(0);
    }
}
