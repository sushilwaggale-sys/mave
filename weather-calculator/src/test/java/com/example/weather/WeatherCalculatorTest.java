package com.example.weather;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WeatherCalculatorTest {
    @Test
    void convertsCelsiusToFahrenheit() {
        assertEquals(32.0, WeatherCalculator.celsiusToFahrenheit(0), 0.001);
        assertEquals(98.6, WeatherCalculator.celsiusToFahrenheit(37), 0.001);
    }

    @Test
    void categorizesTemperature() {
        assertEquals("Cold", WeatherCalculator.describe(5));
        assertEquals("Mild", WeatherCalculator.describe(18));
        assertEquals("Hot", WeatherCalculator.describe(30));
    }

    @Test
    void rejectsNonFiniteTemperatures() {
        assertThrows(IllegalArgumentException.class,
                () -> WeatherCalculator.celsiusToFahrenheit(Double.NaN));
        assertThrows(IllegalArgumentException.class,
                () -> WeatherCalculator.describe(Double.POSITIVE_INFINITY));
    }
}
