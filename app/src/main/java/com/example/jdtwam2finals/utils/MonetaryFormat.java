package com.example.jdtwam2finals.utils;

/**
 * Utility Class for Monetary formatting
 */
public class MonetaryFormat {
    /**
     * Formats the currency
     *
     * @param balance
     * @return formattedValue
     */
    public static String formatCurrencyWithTrim(double balance) {
        String formattedValue;

        if (balance == 0) {
            // If the balance is 0, return "0" without any decimal places.
            formattedValue = "0";
        } else if (balance < 1000) {
            // If the balance is less than 1000, format it without decimal places.
            formattedValue = String.valueOf(balance);
        } else if (balance < 1000000) {
            // If the balance is in thousands, format it as Xk (e.g., 1.5k).
            double thousands = balance / 1000;
            formattedValue = String.format("%.1fk", thousands);
        } else if (balance < 1000000000) {
            // If the balance is in millions, format it as Xm (e.g., 1.5m).
            double millions = balance / 1000000;
            formattedValue = String.format("%.1fm", millions);
        } else {
            // If the balance is in billions or more, format it as Xb (e.g., 1.5b).
            double billions = balance / 1000000000;
            formattedValue = String.format("%.1fb", billions);
        }

        // Remove trailing decimal point and zeros if they are all zeros
        formattedValue = formattedValue.replaceAll("\\.0*$", "").replaceAll("\\.$", "");

        return formattedValue;
    }
}
