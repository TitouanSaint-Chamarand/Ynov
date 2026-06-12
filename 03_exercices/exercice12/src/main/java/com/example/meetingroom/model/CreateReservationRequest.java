package com.example.meetingroom.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateReservationRequest(
        @NotNull(message = "L'identifiant de la salle est obligatoire")
        Long roomId,

        @NotBlank(message = "Le nom de la personne qui réserve est obligatoire")
        String reserverName,

        @NotNull(message = "La date/heure de début est obligatoire")
        LocalDateTime startTime,

        @NotNull(message = "La date/heure de fin est obligatoire")
        LocalDateTime endTime
) {
}
