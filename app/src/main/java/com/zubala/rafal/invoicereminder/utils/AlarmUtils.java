package com.zubala.rafal.invoicereminder.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.zubala.rafal.invoicereminder.sync.AlarmReceiver;

import java.util.Calendar;

/**
 * Created by rzubala on 08.03.18.
 */

public class AlarmUtils {

    private static final int INVOICE_REMINDER_PENDING_INTENT_ID = 1435;

    public static void startAlarm(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(   //TODO to service
                context,
                INVOICE_REMINDER_PENDING_INTENT_ID,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        int interval = 1000 * 60 * 1; //TODO 24h
        int hour = 7;   //TODO pref
        int minute = 45; //TODO pref

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, pendingIntent);
    }
}
