package com.example.bank.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequest(
        @NotBlank(message = "Le compte emetteur est obligatoire")
        String fromNumber,

        @NotBlank(message = "Le compte destinataire est obligatoire")
        String toNumber,

        @NotNull(message = "Le montant est obligatoire")
        @Positive(message = "Le montant doit etre strictement positif")
        BigDecimal amount
) {
}
