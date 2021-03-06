package com.zubala.rafal.invoicereminder.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Action;
import android.support.v4.content.ContextCompat;

import com.zubala.rafal.invoicereminder.MainActivity;
import com.zubala.rafal.invoicereminder.R;
import com.zubala.rafal.invoicereminder.data.InvoiceContract;
import com.zubala.rafal.invoicereminder.sync.InvoiceReminderIntentService;
import com.zubala.rafal.invoicereminder.sync.ReminderTasks;

public class NotificationUtils {

    private static final int INVOICE_REMINDER_NOTIFICATION_ID = 1000;
    private static final int INVOICE_REMINDER_PENDING_INTENT_ID = 1235;
    private static final String INVOICE_REMINDER_NOTIFICATION_CHANNEL_ID = "invoice_reminder_notification_channel";
    private static final int ACTION_PAY_PENDING_INTENT_ID = 20;
    private static final int ACTION_IGNORE_PENDING_INTENT_ID = 21;

    public static void clearAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static void clearNotification(Context context, int id) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(INVOICE_REMINDER_NOTIFICATION_ID + id);
    }

    public static void remindUserAboutInvoice(Context context, Cursor data) {
        Integer id = data.getInt(data.getColumnIndex(InvoiceContract.InvoiceEntry._ID));
        String description = data.getString(data.getColumnIndex(InvoiceContract.InvoiceEntry.COLUMN_DESCRIPTION));
        Double amount = data.getDouble(data.getColumnIndex(InvoiceContract.InvoiceEntry.COLUMN_AMOUNT));
        String currency = data.getString(data.getColumnIndex(InvoiceContract.InvoiceEntry.COLUMN_CURRENCY));
        if (id == null || amount == null) {
            return;
        }
        if (currency == null) {
            currency = "";
        }
        if (description == null) {
            description = "";
        }
        String title = context.getString(R.string.reminder_notification_title) + " " + NumberUtils.formatNumberCurrency(amount) + " " + currency;
        String content = context.getString(R.string.reminder_notification_body) + " " + description;

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(INVOICE_REMINDER_NOTIFICATION_CHANNEL_ID, context.getString(R.string.main_notification_channel_name), NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, INVOICE_REMINDER_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_sale_time)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.reminder_notification_body)))
                .setContentIntent(contentIntent(context))
                .addAction(markInvoiceAction(context, id))
                .addAction(ignoreReminderAction(context, id))
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        notificationManager.notify(INVOICE_REMINDER_NOTIFICATION_ID + id, notificationBuilder.build());
    }

    private static Action ignoreReminderAction(Context context, int id) {
        Intent ignoreReminderIntent = new Intent(context, InvoiceReminderIntentService.class);
        ignoreReminderIntent.setAction(ReminderTasks.ACTION_DISMISS_NOTIFICATION);
        Uri uriForInvoiceClicked = InvoiceContract.InvoiceEntry.buildInvoiceUriWithId(id);
        ignoreReminderIntent.setData(uriForInvoiceClicked);
        PendingIntent ignoreReminderPendingIntent = PendingIntent.getService(
                context,
                ACTION_IGNORE_PENDING_INTENT_ID,
                ignoreReminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Action ignoreReminderAction = new Action(R.drawable.ic_close_black_24dp, context.getString(R.string.action_cancel), ignoreReminderPendingIntent);
        return ignoreReminderAction;
    }

    private static Action markInvoiceAction(Context context, int id) {
        Intent markInvoiceIntent = new Intent(context, InvoiceReminderIntentService.class);
        markInvoiceIntent.setAction(ReminderTasks.ACTION_PAY_INVOICE);
        Uri uriForInvoiceClicked = InvoiceContract.InvoiceEntry.buildInvoiceUriWithId(id);
        markInvoiceIntent.setData(uriForInvoiceClicked);
        PendingIntent incrementWaterPendingIntent = PendingIntent.getService(
                context,
                ACTION_PAY_PENDING_INTENT_ID,
                markInvoiceIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Action drinkWaterAction = new Action(R.drawable.ic_check_black_24dp, context.getString(R.string.action_pay), incrementWaterPendingIntent);
        return drinkWaterAction;
    }

    private static PendingIntent contentIntent(Context context) {
        Intent startActivityIntent = new Intent(context, InvoiceReminderIntentService.class);
        startActivityIntent.setAction(ReminderTasks.ACTION_OPEN_PAY_ON_TIME);
        return PendingIntent.getService(
                context,
                INVOICE_REMINDER_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context) {
        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.ic_sale_time);
        return largeIcon;
    }
}