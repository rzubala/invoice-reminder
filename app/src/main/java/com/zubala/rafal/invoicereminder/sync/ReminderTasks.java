/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zubala.rafal.invoicereminder.sync;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.zubala.rafal.invoicereminder.InvoiceActivity;
import com.zubala.rafal.invoicereminder.MainActivity;
import com.zubala.rafal.invoicereminder.R;
import com.zubala.rafal.invoicereminder.data.InvoiceContract;
import com.zubala.rafal.invoicereminder.utils.AlarmUtils;
import com.zubala.rafal.invoicereminder.utils.NotificationUtils;

import java.util.LinkedList;
import java.util.List;

public class ReminderTasks {

    public static final String ACTION_PAY_INVOICE = "pay-invoice";
    public static final String ACTION_OPEN_PAY_ON_TIME = "open-pay-on-time";
    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";
    public static final String ACTION_CHECK_INVOICE_FOR_NOTIFICATION = "check-invoice-notification";
    private static final String TAG = ReminderTasks.class.getSimpleName();

    public static void executeTask(Context context, String action, Uri uri) {
        if (ACTION_PAY_INVOICE.equals(action)) {
            payInvoice(context, uri);
        } else if (ACTION_DISMISS_NOTIFICATION.equals(action)) {
            clearNotification(context, uri);
        } else if (ACTION_CHECK_INVOICE_FOR_NOTIFICATION.equals(action)) {
            Log.d(TAG, "Check for invoices for notification");
            checkInvoicesForNotification(context, uri);
        } else if (ACTION_OPEN_PAY_ON_TIME.equals(action)) {
            Intent mainIntent = new Intent(context, MainActivity.class);
            context.startActivity(mainIntent);
            NotificationUtils.clearAllNotifications(context);
        }
    }

    private static void checkInvoicesForNotification(final Context context, Uri uri) {
        AsyncTask mBackgroundTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                findInvoices(context, InvoiceContract.InvoiceEntry.CONTENT_URI);
                return null;
            }
        };
        mBackgroundTask.execute();
    }

    private static void findInvoices(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, //TODO only with date and days number
                null,
                getSelectionArgs(),
                getSelectionArguments(),
                InvoiceContract.InvoiceEntry.COLUMN_DATE);
        if (cursor == null) {
            return;
        }
        int size = cursor.getCount();
        for (int i=0;i<size;i++) {
            cursor.moveToPosition(i);
            NotificationUtils.remindUserAboutInvoice(context, cursor);
        }
    }

    private static String getSelectionArgs() {
        return null;
    }

    private static String[] getSelectionArguments() {
        return null;
    }

    private static void payInvoice(Context context, Uri uri) {
        Intent invoiceDetailIntent = new Intent(context, InvoiceActivity.class);
        invoiceDetailIntent.setData(uri);
        context.startActivity(invoiceDetailIntent);

        clearNotification(context, uri);
    }

    private static void clearNotification(Context context, Uri uri) {
        String idString = uri.getLastPathSegment();
        try {
            Integer id = Integer.valueOf(idString);
            NotificationUtils.clearNotification(context, id);
        } catch (Exception ex) {
        }
    }
}