package com.gdevelopers.movies.helpers;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateHelper {
    private static final String date = "yyyy-MM-dd";

    private DateHelper() {
    }

    public static String formatDate(String dateStr) {
        if (dateStr == null || dateStr.equals(""))
            return "";

        final SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
        SimpleDateFormat format = new SimpleDateFormat(date, Locale.US);
        try {
            Date date = format.parse(dateStr);
            return sdf.format(date);
        } catch (ParseException e) {
            Log.d(Constants.STRINGS.EXCEPTION, e.getMessage());
        }
        return "";
    }

    public static String formatYearDate(String dateStr) {
        if (dateStr.equals(""))
            return "";

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.US);
        SimpleDateFormat format = new SimpleDateFormat(date, Locale.US);
        try {
            Date date = format.parse(dateStr);
            return sdf.format(date);
        } catch (ParseException e) {
            Log.d(Constants.STRINGS.EXCEPTION, e.getMessage());
        }
        return "";
    }

    public static String getDays(String string) {
        SimpleDateFormat sdf = new SimpleDateFormat(date, Locale.US);
        try {
            Date date = sdf.parse(string);
            Date now = new Date(System.currentTimeMillis());
            long days = getDateDiff(date, now);
            return String.valueOf(days);
        } catch (ParseException e) {
            Log.d(Constants.STRINGS.EXCEPTION, e.getMessage());
        }
        return "ERROR";
    }

    private static long getDateDiff(Date date1, Date date2) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
}
