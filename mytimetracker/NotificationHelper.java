package com.hfad.mytimetracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Paris on 4/21/2018.
 */

public class NotificationHelper {

    public void addDigestNotif(Context context, Date date){
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, date.getHours());
        calendar.set(Calendar.MINUTE, date.getMinutes());
        calendar.set(Calendar.SECOND, date.getSeconds());
        calendar.set(Calendar.MILLISECOND, 0);

        Intent intent = new Intent(context, DigestPublisher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(manager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void createTaskNotif(Context context, int id, int hour, int minute, int second, String name){
        Calendar calendar = Calendar.getInstance();

//        TimeTrackerDataBaseHelper db = TimeTrackerDataBaseHelper.getInstance(context);
//        SQLiteDatabase read = db.getReadableDatabase();
//        Cursor cursor = read.rawQuery("SELECT MAX(_ID) FROM TASK_INFORMATION", null);
//        cursor.moveToFirst();

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.MINUTE, -15);

        Intent intent = new Intent(context, NotificationPublisher.class);
        intent.putExtra("TASK_NAME", name);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        manager.set(manager.RTC, calendar.getTimeInMillis(), pendingIntent);
    }

    public void deleteTaskNotif(Context context, int id, String name){
        Intent intent = new Intent(context, NotificationPublisher.class);
        intent.putExtra("TASK_NAME", name);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
    }

}
