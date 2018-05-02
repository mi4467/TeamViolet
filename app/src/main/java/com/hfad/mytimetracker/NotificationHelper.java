package com.hfad.mytimetracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Arrays;
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

    public static void createTaskNotif(Context context, Cursor id){
        Calendar calendar = Calendar.getInstance();
        id.moveToFirst();
        String[] dateRep = id.getString(2).split("-");
        Log.d("NotificationDebug", Arrays.toString(dateRep));

        String[] startTime = id.getString(3).split("-");
        Log.d("NotificationDebug", Arrays.toString(startTime));


//        TimeTrackerDataBaseHelper db = TimeTrackerDataBaseHelper.getInstance(context);
//        SQLiteDatabase read = db.getReadableDatabase();
//        Cursor cursor = read.rawQuery("SELECT MAX(_ID) FROM TASK_INFORMATION", null);
//        cursor.moveToFirst();
        calendar.set(Calendar.YEAR, Integer.parseInt(dateRep[0]));
        calendar.set(Calendar.MONTH, Integer.parseInt(dateRep[1])-1);
        calendar.set(Calendar.DATE, Integer.parseInt(dateRep[2]));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTime[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(startTime[1]));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.MINUTE, -15);

        Log.d("AlarmDebug", calendar.getTime().toString());

        Intent intent = new Intent(context, NotificationPublisher.class);
        intent.putExtra("TASK_NAME", id.getString(1));
        intent.putExtra("ID", id.getInt(0));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id.getInt(0), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        manager.set(manager.RTC, calendar.getTimeInMillis(), pendingIntent);
    }

    public static void deleteTaskNotif(Context context, Cursor id){
        Intent intent = new Intent(context, NotificationPublisher.class);
        id.moveToFirst();
        intent.putExtra("TASK_NAME", id.getString(1));
        intent.putExtra("ID", id.getInt(0));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id.getInt(0), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
    }

}