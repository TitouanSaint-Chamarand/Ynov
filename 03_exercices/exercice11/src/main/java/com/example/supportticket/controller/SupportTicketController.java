package com.example.supportticket.controller;

import com.example.supportticket.model.CreateTicketRequest;
import com.example.supportticket.model.TicketResponse;
import com.example.supportticket.model.UpdateTicketStatusRequest;
import com.example.supportticket.service.SupportTicketService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class SupportTicketController {

    private final SupportTicketService service;

    public SupportTicketController(SupportTicketService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody CreateTicketRequest request) {
        var createdTicket = service.create(request.title(), request.priority());
        var response = TicketResponse.from(createdTicket);

        return ResponseEntity
                .created(URI.create("/api/tickets/" + response.id()))
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getById(@PathVariable Long id) {
        var ticket = service.getById(id);
        return ResponseEntity.ok(TicketResponse.from(ticket));
    }

    @GetMapping
    public ResponseEntity<List<TicketResponse>> findAll() {
        var responses = service.findAll()
                .stream()
                .map(TicketResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTicketStatusRequest request
    ) {
        var updatedTicket = service.updateStatus(id, request.status());
        return ResponseEntity.ok(TicketResponse.from(updatedTicket));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        service.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
