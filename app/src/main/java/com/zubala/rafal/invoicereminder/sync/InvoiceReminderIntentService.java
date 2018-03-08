package com.zubala.rafal.invoicereminder.sync;

import android.app.IntentService;
import android.content.Intent;

public class InvoiceReminderIntentService extends IntentService {

    public InvoiceReminderIntentService() {
        super("InvoiceReminderIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        ReminderTasks.executeTask(this, action, intent.getData());
    }
}