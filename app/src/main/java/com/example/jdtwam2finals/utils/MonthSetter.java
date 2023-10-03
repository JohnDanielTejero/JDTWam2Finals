package com.example.jdtwam2finals.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
    public static Date parseDate(String dateString) {
        SimpleDateFormat[] dateFormats = {
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US),
                new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US)
        };

        for (SimpleDateFormat dateFormat : dateFormats) {
            try {
                return dateFormat.parse(dateString);
            } catch (ParseException e) {

            }
        }
        // Handle the format with "+08:00" time zone offset
        if (dateString != null && dateString.length() >= 19) {
            String dateWithoutTimeZone = dateString.substring(0, 19); // Extract date and time part
            String timeZoneOffset = dateString.substring(19); // Extract time zone offset part

            try {
                SimpleDateFormat customDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                Date parsedDate = customDateFormat.parse(dateWithoutTimeZone);

                // Calculate the offset in milliseconds
                int timeZoneOffsetMillis = Integer.parseInt(timeZoneOffset.substring(1, 3)) * 3600000 +
                        Integer.parseInt(timeZoneOffset.substring(4, 6)) * 60000;

                // Adjust the parsed date by adding the time zone offset
                parsedDate.setTime(parsedDate.getTime() - timeZoneOffsetMillis);

                return parsedDate;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // If none of the formats match, return null or handle it as needed
        return null;
    }

}
