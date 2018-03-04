package com.zubala.rafal.invoicereminder.data;

import android.content.Context;

import com.zubala.rafal.invoicereminder.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kolarz on 04.03.18.
 */

public class DateUtils {
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
}
