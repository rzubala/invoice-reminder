package com.zubala.rafal.invoicereminder.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by rzubala on 08.03.18.
 */

public class AlarmReceiver extends BroadcastReceiver {  //TODO change to service
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();  //TODO check invoices and send notifications
    }
}
