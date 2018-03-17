package com.zubala.rafal.invoicereminder.utils;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.zubala.rafal.invoicereminder.R;
import com.zubala.rafal.invoicereminder.data.InvoiceContract;

/**
 * Created by rzubala on 17.03.18.
 */

public class PeriodicUtils {

    private static final int LIMIT = 24;

    public static final String TAG = PeriodicUtils.class.getSimpleName();

    public static void createPeriodicPayments(Context context, long timestampFrom, long timestampTo, PeriodicType type, int frequency, ContentValues contentValues) {

        long timestamp = timestampFrom;
        int cnt = 0;

        while (timestamp < timestampTo) {
            contentValues.put(InvoiceContract.InvoiceEntry.COLUMN_DATE, timestamp);
            Uri uri = context.getContentResolver().insert(InvoiceContract.InvoiceEntry.CONTENT_URI, contentValues);
            cnt++;
            Log.d(TAG, cnt + " insert at "+DateUtils.toDateStr(timestamp));
            if (cnt == LIMIT) {
                Toast.makeText(context, context.getString(R.string.periodic_limit_warning)+ ": "+ LIMIT, Toast.LENGTH_LONG).show();
                break;
            }
            timestamp = incrementTimestamp(timestamp, type, frequency);
        }
    }

    private static long incrementTimestamp(long timestamp, PeriodicType type, int frequency) {
        if (type == PeriodicType.MONTH) {
            timestamp = DateUtils.addMonths(timestamp, frequency);
        } else if (type == PeriodicType.WEEK) {
            timestamp = DateUtils.addWeeks(timestamp, frequency);
        } else if (type == PeriodicType.DAY) {
            timestamp = DateUtils.addDays(timestamp, frequency);
        }
        return timestamp;
    }

    public enum PeriodicType {MONTH, WEEK, DAY}
}
