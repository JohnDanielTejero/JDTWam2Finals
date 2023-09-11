package com.example.jdtwam2finals.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Utility for getting Month
 *
 */
public class MonthSetter {
    /**
     * Returns current month as string
     * @return String
     */
    public static String currentMonth(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");
        return monthFormat.format(calendar.getTime());
    }
}
