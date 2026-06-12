package com.example.meetingroom.controller;

import com.example.meetingroom.exception.ReservationConflictException;
import com.example.meetingroom.exception.ReservationNotFoundException;
import com.example.meetingroom.model.Reservation;
import com.example.meetingroom.model.ReservationStatus;
import com.example.meetingroom.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ReservationController.class, MeetingRoomExceptionHandler.class})
class ReservationControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

    private final LocalDateTime start = LocalDateTime.of(2026, 6, 12, 10, 0);
    private final LocalDateTime end = LocalDateTime.of(2026, 6, 12, 11, 0);

    @Test
    void shouldReturnCreated_whenReservationIsValid() throws Exception {
        // Arrange
        when(reservationService.create(eq(1L), eq("Alice"), any(), any()))
                .thenReturn(new Reservation(1L, 1L, "Alice", start, end, ReservationStatus.CONFIRMED));

        // Act + Assert
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "roomId": 1,
                                  "reserverName": "Alice",
                                  "startTime": "2026-06-12T10:00:00",
                                  "endTime": "2026-06-12T11:00:00"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.roomId").value(1))
                .andExpect(jsonPath("$.reserverName").value("Alice"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void shouldReturnNotFound_whenReservationDoesNotExist() throws Exception {
        // Arrange
        when(reservationService.getById(99L)).thenThrow(new ReservationNotFoundException(99L));

        // Act + Assert
        mockMvc.perform(get("/api/reservations/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Aucune réservation trouvée avec l'identifiant 99"));

        verify(reservationService).getById(99L);
    }

    @Test
    void shouldReturnConflict_whenSlotOverlaps() throws Exception {
        // Arrange
        when(reservationService.create(eq(1L), eq("Alice"), any(), any()))
                .thenThrow(new ReservationConflictException("Le créneau chevauche une réservation existante pour cette salle"));

        // Act + Assert
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "roomId": 1,
                                  "reserverName": "Alice",
                                  "startTime": "2026-06-12T10:00:00",
                                  "endTime": "2026-06-12T11:00:00"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }
}
