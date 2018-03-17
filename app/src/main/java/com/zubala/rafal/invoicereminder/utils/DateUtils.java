package com.zubala.rafal.invoicereminder.utils;

import android.content.Context;

import com.zubala.rafal.invoicereminder.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by kolarz on 04.03.18.
 */

public class DateUtils {

    public static final long DAY_IN_MILLIS = TimeUnit.DAYS.toMillis(1);

    public static final long WEEK_IN_MILLIS = 7 * DAY_IN_MILLIS;

    public static Date parseDate(Context context, String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.dateFormat), Locale.getDefault());
        try {
            return sdf.parse(dateStr);
        } catch (Exception ex) {
            return null;
        }
    }

    public static String formatDate(Context context, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.dateFormat), Locale.getDefault());
        return sdf.format(date);
    }

    public static long addDays(long timestamp, int days) {
        return timestamp + days * DAY_IN_MILLIS;
    }

    public static long addWeeks(long timestamp, int weeks) {
        return timestamp + weeks * WEEK_IN_MILLIS;
    }

    public static long addMonths(long timestamp, int months) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(timestamp);
        calendar.add(Calendar.MONTH, months);
        return calendar.getTimeInMillis();
    }

    public static long toDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(year, month - 1, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return DateUtils.normalizeDate(calendar.getTimeInMillis());
    }

    public static long toUTCTimestamp(Date date) {
        Calendar calendarDate = Calendar.getInstance();
        calendarDate.setTime(date);
        return toDate(calendarDate.get(Calendar.YEAR), calendarDate.get(Calendar.MONTH) + 1, calendarDate.get(Calendar.DAY_OF_MONTH));
    }

    public static long getSqlSelectionForToday() {
        long normalizedUtcNow = normalizeDate(System.currentTimeMillis());
        return normalizedUtcNow;
    }

    public static long normalizeDate(long date) {
        long daysSinceEpoch = elapsedDaysSinceEpoch(date);
        long millisFromEpochToTodayAtMidnightUtc = daysSinceEpoch * DAY_IN_MILLIS;
        return millisFromEpochToTodayAtMidnightUtc;
    }

    private static long elapsedDaysSinceEpoch(long utcDate) {
        return TimeUnit.MILLISECONDS.toDays(utcDate);
    }

    public static String toDateStr(long timestamp) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(timestamp);
        return calendar.get(Calendar.YEAR)+"-"
                +String.format("%02d", calendar.get(Calendar.MONTH)+1)
                +"-"+String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH))
                + " " + String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY))
                +":"+String.format("%02d", calendar.get(Calendar.MINUTE))
                +":"+String.format("%02d", calendar.get(Calendar.SECOND))
                +"."+String.format("%02d", calendar.get(Calendar.MILLISECOND));
    }
}
