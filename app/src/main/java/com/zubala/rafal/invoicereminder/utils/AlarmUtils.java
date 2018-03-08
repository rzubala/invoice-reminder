package com.zubala.rafal.invoicereminder.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.zubala.rafal.invoicereminder.InvoiceActivity;
import com.zubala.rafal.invoicereminder.R;
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

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean showNotification = sharedPreferences.getBoolean(context.getString(R.string.pref_show_notification_key), context.getResources().getBoolean(R.bool.pref_show_notification));

        Intent alarmIntent = new Intent(context, InvoiceReminderIntentService.class);
        alarmIntent.setAction(ReminderTasks.ACTION_CHECK_INVOICE_FOR_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getService(
                context,
                INVOICE_REMINDER_PENDING_INTENT_ID,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if (!showNotification) {
            manager.cancel(pendingIntent);
            Log.d(TAG, "Alarm canceled");
            return;
        }

        int interval = 1000 * 60 * 60 * 24;
        int value = sharedPreferences.getInt(context.getString(R.string.pref_notification_time_key), 12*60);
        int hour = value / 60;
        int minute = value % 60;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, pendingIntent);

        Log.d(TAG, "Alarm scheduled: "+ String.format("%02d", hour)+":"+String.format("%02d", minute));
    }
}
