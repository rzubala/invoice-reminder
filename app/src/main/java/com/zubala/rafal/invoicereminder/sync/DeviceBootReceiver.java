package com.zubala.rafal.invoicereminder.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zubala.rafal.invoicereminder.utils.AlarmUtils;

/**
 * Created by rzubala on 08.03.18.
 */

public class DeviceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.d(AlarmUtils.TAG, "start alarm on boot");
            AlarmUtils.startAlarm(context);
        }
    }
}
