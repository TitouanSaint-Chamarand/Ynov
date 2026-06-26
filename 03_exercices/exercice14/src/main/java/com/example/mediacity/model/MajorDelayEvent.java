package com.example.mediacity.model;

import java.time.LocalDate;

public record MajorDelayEvent(String memberId, LocalDate returnDate, long daysLate) {
}
