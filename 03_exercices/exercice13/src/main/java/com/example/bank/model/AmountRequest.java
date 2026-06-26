package com.example.bank.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AmountRequest(
        @NotNull(message = "Le montant est obligatoire")
        @Positive(message = "Le montant doit etre strictement positif")
        BigDecimal amount
) {
}
