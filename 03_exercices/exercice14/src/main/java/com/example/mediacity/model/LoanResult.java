package com.example.mediacity.model;

import java.time.LocalDate;

public sealed interface LoanResult permits LoanResult.Accepted, LoanResult.Rejected {

    record Accepted(Loan loan) implements LoanResult {
    }

    record Rejected(String reason) implements LoanResult {
    }
}
