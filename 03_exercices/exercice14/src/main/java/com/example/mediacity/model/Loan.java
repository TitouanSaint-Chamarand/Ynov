package com.example.mediacity.model;

import java.time.LocalDate;

public record Loan(
        long id,
        String memberId,
        String workId,
        LocalDate loanDate,
        LocalDate dueDate,
        LocalDate returnDate,
        double penaltyAmount
) {

    public boolean isActive() {
        return returnDate == null;
    }

    public Loan withReturn(LocalDate date, double penalty) {
        return new Loan(id, memberId, workId, loanDate, dueDate, date, penalty);
    }
}
