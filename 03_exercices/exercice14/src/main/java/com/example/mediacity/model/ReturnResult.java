package com.example.mediacity.model;

public sealed interface ReturnResult permits ReturnResult.Completed {

    record Completed(Loan loan, long daysLate, boolean majorDelayRecorded) implements ReturnResult {
    }
}
