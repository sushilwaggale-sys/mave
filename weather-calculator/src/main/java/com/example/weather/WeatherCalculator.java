package com.example.weather;

public final class WeatherCalculator {
    private WeatherCalculator() {}

    public static double celsiusToFahrenheit(double celsius) {
        if (!Double.isFinite(celsius)) {
            throw new IllegalArgumentException("Temperature must be a finite number.");
        }
        return (celsius * 9.0 / 5.0) + 32.0;
    }

    public static String describe(double celsius) {
        if (!Double.isFinite(celsius)) {
            throw new IllegalArgumentException("Temperature must be a finite number.");
        }
        if (celsius < 10) return "Cold";
        if (celsius < 25) return "Mild";
        return "Hot";
    }
}
