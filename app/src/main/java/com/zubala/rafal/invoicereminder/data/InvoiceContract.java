package com.zubala.rafal.invoicereminder.data;

import android.net.Uri;
import android.provider.BaseColumns;

import java.util.concurrent.TimeUnit;

/**
 * Created by rzubala on 01.03.18.
 */

public class InvoiceContract {
    public static final String AUTHORITY = "com.zubala.rafal.invoicereminder";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_INVOICES = "invoices";

    public static final class InvoiceEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_INVOICES).build();

        public static final String TABLE_NAME = "invoices";

        public static final String COLUMN_DESCRIPTION = "description";

        public static final String COLUMN_AMOUNT = "amount";

        public static final String COLUMN_CURRENCY = "currency";

        public static final String COLUMN_DATE = "date";

        public static final String COLUMN_PAID = "paid";

        public static final long DAY_IN_MILLIS = TimeUnit.DAYS.toMillis(1);

        public static Uri buildWeatherUriWithId(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(""+id)
                    .build();
        }

        public static long getSqlSelectionForTodayOnwards() {
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
    }
}
