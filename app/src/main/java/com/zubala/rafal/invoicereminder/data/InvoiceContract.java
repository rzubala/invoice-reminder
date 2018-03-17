package com.zubala.rafal.invoicereminder.data;

import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.zubala.rafal.invoicereminder.utils.AlarmUtils;
import com.zubala.rafal.invoicereminder.utils.DateUtils;

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

        public static Uri buildInvoiceUriWithId(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(""+id)
                    .build();
        }
    }
}
