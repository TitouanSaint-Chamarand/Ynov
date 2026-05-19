package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GradingCalculatorTest {

    @Test
    void score95Attendance90ReturnsA() {
        // Arange
        GradingCalculator calculator = new GradingCalculator(95, 90);

        // Act
        char result = calculator.getGrade();

        // Assert
        assertEquals('A', result);

    }

    @Test
    void score85Attendance90ReturnsB() {
        GradingCalculator calculator = new GradingCalculator(85, 90);

        char result = calculator.getGrade();

        assertEquals('B', result);
    }

    @Test
    void score65Attendance90ReturnsC() {
        GradingCalculator calculator = new GradingCalculator(65, 90);

        char result = calculator.getGrade();

        assertEquals('C', result);
    }

    @Test
    void score95Attendance65ReturnsB() {
        GradingCalculator calculator = new GradingCalculator(95, 65);

        char result = calculator.getGrade();

        assertEquals('B', result);
    }

    @Test
    void score95Attendance55ReturnsF() {
        GradingCalculator calculator = new GradingCalculator(95, 55);

        char result = calculator.getGrade();

        assertEquals('F', result);
    }

    @Test
    void score65Attendance55ReturnsF() {
        GradingCalculator calculator = new GradingCalculator(65, 55);

        char result = calculator.getGrade();

        assertEquals('F', result);
    }

    @Test
    void score50Attendance90ReturnsF() {
        GradingCalculator calculator = new GradingCalculator(50, 90);

        char result = calculator.getGrade();

        assertEquals('F', result);
    }
}
