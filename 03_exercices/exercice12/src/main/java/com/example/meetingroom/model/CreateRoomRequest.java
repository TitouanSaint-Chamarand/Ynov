package com.example.meetingroom.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateRoomRequest(
        @NotBlank(message = "Le nom de la salle est obligatoire")
        String name,

        @Min(value = 1, message = "La capacité doit être supérieure ou égale à 1")
        int capacity
) {
}
