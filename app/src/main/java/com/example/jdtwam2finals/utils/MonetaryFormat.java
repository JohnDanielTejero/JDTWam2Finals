package com.example.jdtwam2finals.utils;

import java.text.DecimalFormat;

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
            // If the balance is less than 1000, format it with one decimal place.
            formattedValue = formatWithOneDecimalPlace(balance);
        } else if (balance < 1000000) {
            // If the balance is in thousands, format it as X.Xk (e.g., 1.5k).
            double thousands = Math.floor(balance / 1000 * 10) / 10;
            formattedValue = formatWithOneDecimalPlace(thousands) + "k";
        } else if (balance < 1000000000) {
            // If the balance is in millions, format it as X.Xm (e.g., 1.5m).
            double millions = Math.floor(balance / 1000000 * 10) / 10;
            formattedValue = formatWithOneDecimalPlace(millions) + "m";
        } else {
            // If the balance is in billions or more, format it as X.Xb (e.g., 1.5b).
            double billions = Math.floor(balance / 1000000000 * 10) / 10;
            formattedValue = formatWithOneDecimalPlace(billions) + "b";
        }

        return formattedValue;
    }

    /**
     * Format a double value with one decimal place and remove trailing zeros if present.
     *
     * @param value
     * @return formattedValue
     */
    private static String formatWithOneDecimalPlace(double value) {
        DecimalFormat df = new DecimalFormat("0.0");
        String formatted = df.format(value);

        // Remove the trailing ".0" if present
        if (formatted.endsWith(".0")) {
            formatted = formatted.substring(0, formatted.length() - 2);
        }

        return formatted;
    }
}
