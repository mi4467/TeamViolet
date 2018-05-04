package com.hfad.mytimetracker;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Paris on 4/16/2018.
 */


public class NotificationPublisher extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent){
        Log.d("AlarmDebug", "It got to");
        final int NOTIFY_ID = 1002;
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.MINUTE, 15);
        NotificationManager notificationmanager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        String id = "my_package_channel_1";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        String name = "my_package_channel";
        String description = "my_package_first_channel";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = notificationmanager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, name, importance);
                mChannel.setDescription(description);
                mChannel.enableVibration(true);
                notificationmanager.createNotificationChannel(mChannel);
            }
        }

        PendingIntent contentIntent = PendingIntent.getActivity(context, 1, new Intent (context,   MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notif = new NotificationCompat.Builder(context, id)
                .setSmallIcon(R.drawable.clock_icon)
                .setContentTitle("MyTimeTracker Task")
                .setContentText(intent.getStringExtra("TASK_NAME") + " begins at " + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE))
                .setContentIntent(contentIntent)
                .build();

        Log.d("AlarmDebug", "It got here");

        notificationmanager.notify(NOTIFY_ID,notif);
    }
}