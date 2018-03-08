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
import android.net.Uri;

import com.zubala.rafal.invoicereminder.InvoiceActivity;
import com.zubala.rafal.invoicereminder.MainActivity;
import com.zubala.rafal.invoicereminder.data.InvoiceContract;
import com.zubala.rafal.invoicereminder.utils.NotificationUtils;

public class ReminderTasks {

    public static final String ACTION_PAY_INVOICE = "pay-invoice";
    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";

    public static void executeTask(Context context, String action) {
        if (ACTION_PAY_INVOICE.equals(action)) {
            payInvoice(context);
        } else if (ACTION_DISMISS_NOTIFICATION.equals(action)) {
            NotificationUtils.clearAllNotifications(context);
        }
    }

    private static void payInvoice(Context context) {
        Intent invoiceDetailIntent = new Intent(context, InvoiceActivity.class);
        //Uri uriForInvoiceClicked = InvoiceContract.InvoiceEntry.buildInvoiceUriWithId(id);
        //invoiceDetailIntent.setData(uriForInvoiceClicked);
        context.startActivity(invoiceDetailIntent);

        NotificationUtils.clearAllNotifications(context);
    }
}