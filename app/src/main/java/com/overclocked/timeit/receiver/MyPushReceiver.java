package com.overclocked.timeit.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.localytics.android.Localytics;
import com.localytics.android.PushTrackingActivity;
import com.overclocked.timeit.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 538280 on 5/12/2016.
 */
public class MyPushReceiver extends BroadcastReceiver {

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if (intent.getExtras().containsKey("ll")) {
            Localytics.handlePushNotificationReceived(intent); // tag push received event
            String message = intent.getStringExtra("message");
            if (!TextUtils.isEmpty(message)) {
                Intent trackingIntent = new Intent(context, PushTrackingActivity.class);
                trackingIntent.putExtras(intent); // add all extras from received intent
                int requestCode = getRequestCode(intent.getExtras());
                PendingIntent contentIntent = PendingIntent.getActivity(context, requestCode, trackingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                        .setSmallIcon(getNotificationIcon())
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setContentIntent(contentIntent)
                        .setColor(context.getResources().getColor(R.color.colorPrimary))
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true);

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(requestCode, builder.build());
                //Play notification sound
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(context, notification);
                r.play();
            }
        }
    }

    /**
     * Get a unique requestCode so we don't override other unopened pushes. The Localytics SDK
     * uses the campaign ID (ca) within the 'll' JSON string extra. Use that value if it exists.
     */
    private int getRequestCode(Bundle extras) {
        int requestCode = 1;
        if (extras != null && extras.containsKey("ll"))
        {
            try
            {
                JSONObject llObject = new JSONObject(extras.getString("ll"));
                requestCode = llObject.getInt("ca");
            }
            catch (JSONException e)
            {
            }
        }
        return requestCode;
    }

    private int getNotificationIcon() {
        boolean whiteIcon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
        return whiteIcon ? R.drawable.ic_watch_white_48dp : R.drawable.ic_launcher;
    }
}
