package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PriceCalculatorTest {

    private static final double DELTA = 1e-9;

    @Test
    void calculateTotalPriceReturnsProductOfUnitPriceAndQuantity() {
        // Arange
        PriceCalculator calculator = new PriceCalculator();

        // Act
        double result = calculator.calculateTotalPrice(10.0, 3);

        // Assert
        assertEquals(30.0, result, DELTA);

    }

    @Test
    void applyDiscountReturnsPriceMinusDiscountPortion() {
        PriceCalculator calculator = new PriceCalculator();

        double result = calculator.applyDiscount(100.0, 0.20);

        assertEquals(80.0, result, DELTA);
    }

    @Test
    void calculateVatReturnsVatAmount() {
        PriceCalculator calculator = new PriceCalculator();

        double result = calculator.calculateVat(100.0, 0.20);

        assertEquals(20.0, result, DELTA);
    }

    @Test
    void calculatePriceWithVatReturnsPricePlusVat() {
        PriceCalculator calculator = new PriceCalculator();

        double result = calculator.calculatePriceWithVat(100.0, 0.20);

        assertEquals(120.0, result, DELTA);
    }

    @Test
    void calculateTotalPriceThrowsWhenUnitPriceIsNegative() {
        PriceCalculator calculator = new PriceCalculator();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculateTotalPrice(-1.0, 1)
        );

        assertEquals("Le prix unitaire ne doit pas être négatif.", exception.getMessage());
    }

    @Test
    void calculateTotalPriceThrowsWhenQuantityIsNegative() {
        PriceCalculator calculator = new PriceCalculator();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculateTotalPrice(10.0, -1)
        );

        assertEquals("La quantité ne doit pas être négative.", exception.getMessage());
    }

    @Test
    void applyDiscountThrowsWhenDiscountRateIsNegative() {
        PriceCalculator calculator = new PriceCalculator();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.applyDiscount(100.0, -0.1)
        );

        assertEquals("Le taux de remise ne doit pas être négatif.", exception.getMessage());
    }

    @Test
    void calculateVatThrowsWhenVatRateIsNegative() {
        PriceCalculator calculator = new PriceCalculator();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculateVat(100.0, -0.05)
        );

        assertEquals("Le taux de TVA ne doit pas être négatif.", exception.getMessage());
    }

    @Test
    void calculatePriceWithVatThrowsWhenVatRateIsNegative() {
        PriceCalculator calculator = new PriceCalculator();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculatePriceWithVat(100.0, -0.05)
        );

        assertEquals("Le taux de TVA ne doit pas être négatif.", exception.getMessage());
    }
}
