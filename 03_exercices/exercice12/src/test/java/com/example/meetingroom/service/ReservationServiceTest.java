package com.example.meetingroom.service;

import com.example.meetingroom.exception.AlreadyCancelledException;
import com.example.meetingroom.exception.ReservationConflictException;
import com.example.meetingroom.exception.RoomNotFoundException;
import com.example.meetingroom.model.Reservation;
import com.example.meetingroom.model.ReservationStatus;
import com.example.meetingroom.model.Room;
import com.example.meetingroom.repository.ReservationRepository;
import com.example.meetingroom.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private ReservationService service;

    private final LocalDateTime start = LocalDateTime.of(2026, 6, 12, 10, 0);
    private final LocalDateTime end = LocalDateTime.of(2026, 6, 12, 11, 0);

    @Test
    void shouldCreateReservation_whenDataIsValid() {
        // Arrange
        when(roomRepository.findById(1L)).thenReturn(Optional.of(new Room(1L, "Salle A", 10)));
        when(reservationRepository.findConfirmedByRoomId(1L)).thenReturn(List.of());
        when(reservationRepository.save(1L, "Alice", start, end, ReservationStatus.CONFIRMED))
                .thenReturn(new Reservation(1L, 1L, "Alice", start, end, ReservationStatus.CONFIRMED));

        // Act
        Reservation result = service.create(1L, "Alice", start, end);

        // Assert
        assertEquals(1L, result.id());
        assertEquals("Alice", result.reserverName());
        assertEquals(ReservationStatus.CONFIRMED, result.status());
        verify(reservationRepository).save(1L, "Alice", start, end, ReservationStatus.CONFIRMED);
    }

    @Test
    void shouldThrowNotFound_whenRoomDoesNotExist() {
        // Arrange
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RoomNotFoundException.class, () -> service.create(99L, "Alice", start, end));
        verify(reservationRepository, never()).save(any(), any(), any(), any(), any());
    }

    @Test
    void shouldThrowBadRequest_whenEndTimeIsBeforeStartTime() {
        // Arrange
        when(roomRepository.findById(1L)).thenReturn(Optional.of(new Room(1L, "Salle A", 10)));

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> service.create(1L, "Alice", end, start)
        );
        verify(reservationRepository, never()).save(any(), any(), any(), any(), any());
    }

    @Test
    void shouldThrowConflict_whenSlotOverlapsExistingReservation() {
        // Arrange
        Reservation existing = new Reservation(1L, 1L, "Bob", start, end, ReservationStatus.CONFIRMED);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(new Room(1L, "Salle A", 10)));
        when(reservationRepository.findConfirmedByRoomId(1L)).thenReturn(List.of(existing));

        LocalDateTime overlappingStart = LocalDateTime.of(2026, 6, 12, 10, 30);
        LocalDateTime overlappingEnd = LocalDateTime.of(2026, 6, 12, 11, 30);

        // Act + Assert
        assertThrows(
                ReservationConflictException.class,
                () -> service.create(1L, "Alice", overlappingStart, overlappingEnd)
        );
        verify(reservationRepository, never()).save(any(), any(), any(), any(), any());
    }

    @Test
    void shouldCancelReservation_whenStatusIsConfirmed() {
        // Arrange
        Reservation confirmed = new Reservation(1L, 1L, "Alice", start, end, ReservationStatus.CONFIRMED);
        Reservation cancelled = new Reservation(1L, 1L, "Alice", start, end, ReservationStatus.CANCELLED);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(confirmed));
        when(reservationRepository.updateStatus(1L, ReservationStatus.CANCELLED)).thenReturn(cancelled);

        // Act
        Reservation result = service.cancel(1L);

        // Assert
        assertEquals(ReservationStatus.CANCELLED, result.status());
        verify(reservationRepository).updateStatus(1L, ReservationStatus.CANCELLED);
    }

    @Test
    void shouldThrowConflict_whenReservationIsAlreadyCancelled() {
        // Arrange
        Reservation cancelled = new Reservation(1L, 1L, "Alice", start, end, ReservationStatus.CANCELLED);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(cancelled));

        // Act + Assert
        assertThrows(AlreadyCancelledException.class, () -> service.cancel(1L));
        verify(reservationRepository, never()).updateStatus(eq(1L), any());
    }
}
