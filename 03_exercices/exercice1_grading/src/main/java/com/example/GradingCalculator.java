package com.example;

public class GradingCalculator {

    private final int score;
    private final int attendancePercentage;

    public GradingCalculator(int score, int attendancePercentage) {
        this.score = score;
        this.attendancePercentage = attendancePercentage;
    }

    public char getGrade() {
        if (score > 90 && attendancePercentage > 70) {
            return 'A';
        }
        if (score > 80 && attendancePercentage > 60) {
            return 'B';
        }
        if (score > 60 && attendancePercentage > 60) {
            return 'C';
        }
        return 'F';
    }
}
