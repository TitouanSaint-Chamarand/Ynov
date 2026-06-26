package com.example.mediacity;

public final class LoanPolicy {

    public static final int LOAN_DURATION_DAYS = 21;
    public static final double PENALTY_PER_DAY = 0.15;
    public static final int MAJOR_DELAY_DAYS = 7;

    private LoanPolicy() {
    }

    public static boolean isMajorDelay(long daysLate) {
        return daysLate >= MAJOR_DELAY_DAYS;
    }

    public static double calculatePenalty(long daysLate) {
        if (daysLate <= 0) {
            return 0.0;
        }
        return daysLate * PENALTY_PER_DAY;
    }
}
