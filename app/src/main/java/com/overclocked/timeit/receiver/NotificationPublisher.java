package com.overclocked.timeit.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.overclocked.timeit.AppController;
import com.overclocked.timeit.R;
import com.overclocked.timeit.activity.HomeActivity;
import com.overclocked.timeit.common.AppConstants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION = "notification_type";
    public static int NOTIFICATION_ID = 155;

    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Calendar calendar = Calendar.getInstance();
        final DateFormat df = new SimpleDateFormat(AppConstants.SWIPE_DATE_FORMAT);
        if(intent.getExtras().getString(NOTIFICATION).equals("swipeOut")) {
            AppController.getInstance().getDatabaseHandler().updateSwipeDateReminder(df.format(calendar.getTime()),0);
            Intent swipeOutIntent = new Intent();
            swipeOutIntent.setAction("SWIPE_OUT_ACTION");
            PendingIntent pendingIntentSwipeOut = PendingIntent.getBroadcast(context, 12345, swipeOutIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Intent penIndent = new Intent(context, HomeActivity.class);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            penIndent,
                            PendingIntent.FLAG_CANCEL_CURRENT);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(getNotificationIcon())
                    .setContentTitle("True Time : Swipe Out")
                    .setContentText("It's time to leave for the day. \uD83D\uDE0E")
                    .addAction(R.drawable.ic_arrow_back_black_18dp, "Swipe Out", pendingIntentSwipeOut)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText("It's time to leave for the day. \uD83D\uDE0E"));
            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.setAutoCancel(true);  // for removing the notification post click
            mBuilder.setColor(context.getResources().getColor(R.color.true_orange));
            notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
        }

    }

    private int getNotificationIcon() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? R.drawable.ic_watch_white_48dp : R.drawable.ic_launcher;
    }
}