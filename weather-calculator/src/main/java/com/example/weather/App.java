package com.example.weather;

public final class App {
    private App() {}

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java ... App <temperature-in-Celsius>");
            System.exit(1);
        }

        try {
            double celsius = Double.parseDouble(args[0]);
            double fahrenheit = WeatherCalculator.celsiusToFahrenheit(celsius);
            System.out.printf("Temperature: %.1f °C (%.1f °F)%n", celsius, fahrenheit);
            System.out.println("Weather: " + WeatherCalculator.describe(celsius));
        } catch (IllegalArgumentException e) {
            System.err.println("Please enter a valid finite temperature in Celsius.");
            System.exit(1);
        }
    }
}
