package com.example.supportticket.service;

import com.example.supportticket.exception.InvalidStatusTransitionException;
import com.example.supportticket.exception.TicketNotFoundException;
import com.example.supportticket.model.Priority;
import com.example.supportticket.model.SupportTicket;
import com.example.supportticket.model.TicketStatus;
import com.example.supportticket.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupportTicketServiceTest {

    @Mock
    private TicketRepository repository;

    @InjectMocks
    private SupportTicketService service;

    @Test
    void shouldCreateTicket_whenTitleAndPriorityAreValid() {
        // Arrange
        when(repository.save("Probleme reseau", Priority.HIGH, TicketStatus.OPEN))
                .thenReturn(new SupportTicket(1L, "Probleme reseau", Priority.HIGH, TicketStatus.OPEN));

        // Act
        SupportTicket result = service.create("Probleme reseau", Priority.HIGH);

        // Assert
        assertEquals(1L, result.id());
        assertEquals("Probleme reseau", result.title());
        assertEquals(Priority.HIGH, result.priority());
        verify(repository).save("Probleme reseau", Priority.HIGH, TicketStatus.OPEN);
    }

    @Test
    void shouldCreateTicketWithOpenStatus_whenTicketIsCreated() {
        // Arrange
        when(repository.save("Bug application", Priority.MEDIUM, TicketStatus.OPEN))
                .thenReturn(new SupportTicket(2L, "Bug application", Priority.MEDIUM, TicketStatus.OPEN));

        // Act
        SupportTicket result = service.create("Bug application", Priority.MEDIUM);

        // Assert
        assertEquals(TicketStatus.OPEN, result.status());
    }

    @Test
    void shouldReturnTicket_whenIdExists() {
        // Arrange
        SupportTicket existingTicket = new SupportTicket(1L, "Ticket existant", Priority.LOW, TicketStatus.OPEN);
        when(repository.findById(1L)).thenReturn(Optional.of(existingTicket));

        // Act
        SupportTicket result = service.getById(1L);

        // Assert
        assertEquals("Ticket existant", result.title());
        verify(repository).findById(1L);
    }

    @Test
    void shouldThrowNotFoundException_whenIdDoesNotExist() {
        // Arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(TicketNotFoundException.class, () -> service.getById(99L));
        verify(repository).findById(99L);
    }

    @Test
    void shouldUpdateStatus_whenTransitionIsAllowed() {
        // Arrange
        SupportTicket openTicket = new SupportTicket(1L, "Ticket", Priority.HIGH, TicketStatus.OPEN);
        SupportTicket inProgressTicket = new SupportTicket(1L, "Ticket", Priority.HIGH, TicketStatus.IN_PROGRESS);

        when(repository.findById(1L)).thenReturn(Optional.of(openTicket));
        when(repository.updateStatus(1L, TicketStatus.IN_PROGRESS)).thenReturn(inProgressTicket);

        // Act
        SupportTicket result = service.updateStatus(1L, TicketStatus.IN_PROGRESS);

        // Assert
        assertEquals(TicketStatus.IN_PROGRESS, result.status());
        verify(repository).updateStatus(1L, TicketStatus.IN_PROGRESS);
    }

    @Test
    void shouldResolveTicket_whenTransitionFromInProgressIsAllowed() {
        // Arrange
        SupportTicket inProgressTicket = new SupportTicket(1L, "Ticket", Priority.MEDIUM, TicketStatus.IN_PROGRESS);
        SupportTicket resolvedTicket = new SupportTicket(1L, "Ticket", Priority.MEDIUM, TicketStatus.RESOLVED);

        when(repository.findById(1L)).thenReturn(Optional.of(inProgressTicket));
        when(repository.updateStatus(1L, TicketStatus.RESOLVED)).thenReturn(resolvedTicket);

        // Act
        SupportTicket result = service.updateStatus(1L, TicketStatus.RESOLVED);

        // Assert
        assertEquals(TicketStatus.RESOLVED, result.status());
    }

    @Test
    void shouldThrowConflictException_whenResolvedTicketIsUpdated() {
        // Arrange
        SupportTicket resolvedTicket = new SupportTicket(1L, "Ticket", Priority.LOW, TicketStatus.RESOLVED);
        when(repository.findById(1L)).thenReturn(Optional.of(resolvedTicket));

        // Act + Assert
        assertThrows(
                InvalidStatusTransitionException.class,
                () -> service.updateStatus(1L, TicketStatus.IN_PROGRESS)
        );
        verify(repository, never()).updateStatus(eq(1L), any());
    }

    @Test
    void shouldThrowConflictException_whenTransitionIsForbidden() {
        // Arrange
        SupportTicket inProgressTicket = new SupportTicket(1L, "Ticket", Priority.HIGH, TicketStatus.IN_PROGRESS);
        when(repository.findById(1L)).thenReturn(Optional.of(inProgressTicket));

        // Act + Assert
        assertThrows(
                InvalidStatusTransitionException.class,
                () -> service.updateStatus(1L, TicketStatus.OPEN)
        );
        verify(repository, never()).updateStatus(eq(1L), any());
    }

    @Test
    void shouldThrowException_whenTitleIsTooShort() {
        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> service.create("ab", Priority.LOW));
        verify(repository, never()).save(any(), any(), any());
    }
}
