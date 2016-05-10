package com.overclocked.timeit.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.overclocked.timeit.AppController;
import com.overclocked.timeit.common.AppConstants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Thahaseen on 5/10/2016.
 */
public class SwipeInOutReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if("SWIPE_OUT_ACTION".equals(action)) {
            Calendar calendar = Calendar.getInstance();
            final DateFormat df = new SimpleDateFormat(AppConstants.SWIPE_DATE_FORMAT);
            AppController.getInstance().getDatabaseHandler().updateSwipeOutTime(df.format(calendar.getTime()), calendar.getTimeInMillis());
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(NotificationPublisher.NOTIFICATION_ID);
        }
    }

}
