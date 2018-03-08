package com.zubala.rafal.invoicereminder.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.zubala.rafal.invoicereminder.InvoiceActivity;
import com.zubala.rafal.invoicereminder.data.InvoiceContract;
import com.zubala.rafal.invoicereminder.sync.InvoiceReminderIntentService;
import com.zubala.rafal.invoicereminder.sync.ReminderTasks;

import java.util.Calendar;
import java.util.TimerTask;

/**
 * Created by rzubala on 08.03.18.
 */

public class AlarmUtils {

    private static final int INVOICE_REMINDER_PENDING_INTENT_ID = 1435;

    private static final String TAG = AlarmUtils.class.getSimpleName();

    public static void startAlarm(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(context, InvoiceReminderIntentService.class);
        alarmIntent.setAction(ReminderTasks.ACTION_CHECK_INVOICE_FOR_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getService(
                context,
                INVOICE_REMINDER_PENDING_INTENT_ID,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        int interval = 1000 * 60 * 20; //TODO 24h
        int hour = 11;   //TODO pref
        int minute = 51; //TODO pref

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, pendingIntent);

        Log.d(TAG, "Alarm scheduled: "+hour+":"+minute);
    }
}
